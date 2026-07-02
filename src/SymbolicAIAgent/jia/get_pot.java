package my.internal.actions;

import jason.asSemantics.*;
import jason.asSyntax.*;
import jason.environment.Environment; 
import java.util.*;

/**
 * Internal Action: get_pot(+ActivePots, +Recipes, +Ingredient, -X, -Y)
 *
 * Description:
 * Selects the "best" active pot that can accept the specified ingredient,
 * based on recipe compatibility and completion potential.
 *
 * Parameters:
 * 1. ActivePots  - List of active pot terms (e.g., [active_pot(2,3), ...])
 * 2. Recipes     - List of recipes, each a list of ingredients
 * 3. Ingredient  - Ingredient to be placed (e.g., onion or tomato)
 * 4. X, Y        - Output coordinates of the chosen pot
 *
 * Behavior:
 * - For each active pot, it checks whether adding the given ingredient
 *   would be valid for any current recipe.
 * - It scores pots based on how well they match a recipe so far.
 * - It heavily prefers pots that would complete a recipe (+10000 bonus).
 * - If a suitable pot is found, it unifies its coordinates with X and Y.
 * - If no valid pot is found, unifies (999,999).
 * - If a pot triggers recipe completion, it also emits `pot_to_cook(X,Y)` as a percept.
 */
public class get_pot extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        // Validate input
        if (args.length != 5) {
            throw new Exception("get_pot requires 5 args: get_pot(ActivePots, Recipes, Ingredient, X, Y)");
        }

        // Parse arguments
        ListTerm activePotsList  = (ListTerm) args[0];
        ListTerm recipesListTerm = (ListTerm) args[1];
        String   newIngredient   = args[2].toString().toLowerCase();
        Term     outXTerm        = args[3];
        Term     outYTerm        = args[4];

        // Convert recipes into count maps (e.g., [onion,onion,tomato] -> {onion=2, tomato=1})
        List<Map<String,Integer>> recipeCountMaps = new ArrayList<>();
        for (Term rTerm : recipesListTerm) {
            if (rTerm instanceof ListTerm) {
                recipeCountMaps.add(buildCountMap((ListTerm) rTerm));
            }
        }

        // Track the best candidate pot
        boolean foundAny = false;
        int     bestScore = -999999;
        int     bestX     = 999;
        int     bestY     = 999;
        boolean triggersCompletion = false;

        // Iterate over each active pot
        for (Term potTerm : activePotsList) {
            if (!(potTerm instanceof Structure)) continue;
            Structure s = (Structure) potTerm;
            if (!s.getFunctor().equals("active_pot") || s.getArity() != 2) {
                continue;
            }

            int potX, potY;
            try {
                potX = Integer.parseInt(s.getTerm(0).toString());
                potY = Integer.parseInt(s.getTerm(1).toString());
            } catch (NumberFormatException e) {
                continue; // skip invalid coordinates
            }

            // Retrieve current pot contents from agent's belief base
            Map<String,Integer> potContents = getPotContents(ts, potX, potY);

            // Compare against each recipe
            for (Map<String,Integer> recipeMap : recipeCountMaps) {
                // Skip this recipe if pot already invalid for it
                if (! potMatchesRecipeSoFar(potContents, recipeMap)) {
                    continue;
                }
                int needed = recipeMap.getOrDefault(newIngredient, 0);
                int have   = potContents.getOrDefault(newIngredient, 0);
                if (needed == 0) {
                    // recipe doesn't need it
                    continue;
                }
                if (have >= needed) {
                    continue;
                }

                // Score the match
                int score = countCorrectIngredients(potContents, recipeMap);

                // Simulate adding the ingredient to see if recipe would complete
                Map<String,Integer> hypothetical = new HashMap<>(potContents);
                hypothetical.put(newIngredient, have+1);
                boolean completes = fullyMatchesRecipe(hypothetical, recipeMap);
                if (completes) {
                    score += 10000;  // big bonus for finishing a recipe
                }

                // Select if best so far
                if (score > bestScore) {
                    bestScore = score;
                    bestX     = potX;
                    bestY     = potY;
                    foundAny  = true;
                    triggersCompletion = completes;
                }
            }
        }

        // Decide final pot or (999,999)
        if (!foundAny) {
            // unify with (999,999)
            boolean r1 = un.unifies(outXTerm, ASSyntax.createNumber(999));
            boolean r2 = un.unifies(outYTerm, ASSyntax.createNumber(999));
            return (r1 && r2);
        } else {
            // Optionally add a percept if the recipe would complete with this addition
            if (triggersCompletion) {
                // Insert pot_to_cook
                Kitchen env = Kitchen.getInstance();
                if (env != null) {
                    String potToCookStr = String.format("pot_to_cook(%d,%d)", bestX, bestY);
                    Literal potToCook   = ASSyntax.parseLiteral(potToCookStr);
                    env.addPercept("staychef", potToCook);
                }
            }
            // unify best pot coords
            boolean r1 = un.unifies(outXTerm, ASSyntax.createNumber(bestX));
            boolean r2 = un.unifies(outYTerm, ASSyntax.createNumber(bestY));
            return (r1 && r2);
        }
    }


    /** Build a map from e.g. [onion,onion,tomato] => {onion=2, tomato=1} */
    private Map<String,Integer> buildCountMap(ListTerm recipeTerm) {
        Map<String,Integer> map = new HashMap<>();
        for (Term ing : recipeTerm) {
            String ingStr = ing.toString().toLowerCase();
            map.put(ingStr, map.getOrDefault(ingStr, 0) + 1);
        }
        return map;
    }

    /** Gather pot_contents(X,Y,Ing,Count) from the agent’s BB */
    private Map<String,Integer> getPotContents(TransitionSystem ts, int px, int py) {
        Map<String,Integer> potContents = new HashMap<>();
        Iterator<Literal> it = ts.getAg().getBB().iterator();
        while (it.hasNext()) {
            Literal b = it.next();
            if (b.getFunctor().equals("pot_contents") && b.getArity() == 4) {
                try {
                    int bx = Integer.parseInt(b.getTerm(0).toString());
                    int by = Integer.parseInt(b.getTerm(1).toString());
                    if (bx == px && by == py) {
                        String ingr = b.getTerm(2).toString().toLowerCase();
                        int count   = Integer.parseInt(b.getTerm(3).toString());
                        potContents.put(ingr, potContents.getOrDefault(ingr, 0) + count);
                    }
                } catch (Exception ex) {
                    // skip
                }
            }
        }
        return potContents;
    }

    /** True if pot does not exceed what the recipe needs */
    private boolean potMatchesRecipeSoFar(Map<String,Integer> potContents, Map<String,Integer> recipeMap) {
        for (Map.Entry<String,Integer> e : potContents.entrySet()) {
            String ing = e.getKey();
            int have   = e.getValue();
            int needed = recipeMap.getOrDefault(ing, 0);
            if (have > needed) {
                return false;
            }
        }
        return true;
    }

    /** True if potContents exactly matches the recipeMap (no extra, no less) */
    private boolean fullyMatchesRecipe(Map<String,Integer> potContents, Map<String,Integer> recipeMap) {
        // check every ingredient that recipe calls for
        for (Map.Entry<String,Integer> e : recipeMap.entrySet()) {
            String ing = e.getKey();
            int needed = e.getValue();
            int have   = potContents.getOrDefault(ing, 0);
            if (have != needed) {
                return false;
            }
        }
        // also ensure pot has no extra ingredients
        for (Map.Entry<String,Integer> e : potContents.entrySet()) {
            String ing  = e.getKey();
            int have    = e.getValue();
            int recipeN = recipeMap.getOrDefault(ing, 0);
            if (have > recipeN) {
                return false;
            }
        }
        return true;
    }

    /** e.g. how many correct ingredients does the pot already have? */
    private int countCorrectIngredients(Map<String,Integer> potContents, Map<String,Integer> recipeMap) {
        int total = 0;
        for (Map.Entry<String,Integer> e : potContents.entrySet()) {
            String ing = e.getKey();
            int have   = e.getValue();
            int need   = recipeMap.getOrDefault(ing, 0);
            // score = sum of min(have, need) for each ingredient
            total += Math.min(have, need);
        }
        return total;
    }
}
