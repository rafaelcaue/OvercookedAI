package my.internal.actions;

import jason.asSemantics.DefaultInternalAction;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Term;
import jason.asSyntax.ASSyntax;
import jason.asSemantics.Unifier;
import jason.asSemantics.TransitionSystem;


/**
 * Internal Action: get_recipe_at_index(Recipes, Index, RecipeOut)
 *
 * Retrieves the recipe at a given index from a list of recipes.
 * Recipes are represented as a list of lists (each inner list is a recipe).
 *
 * Arguments:
 * 1. Recipes      - a ListTerm containing recipe sublists (e.g., [[onion,onion,tomato], ...])
 * 2. Index        - an integer or atom convertible to integer (1-based index)
 * 3. RecipeOut    - a variable to unify with the recipe at the given index
 *
 * Notes:
 * - Indexing is 1-based to match human expectations, but internally adjusted to 0-based.
 * - If the index is out of bounds, it defaults to returning the first recipe.
 */
public class get_recipe_at_index extends DefaultInternalAction {

    
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        
        if (args.length != 3) {
            throw new Exception("get_recipe_at_index requires 3 arguments: get_recipe_at_index(Recipes, Index, RecipeOut)");
        }
        
        // Cast the first argument to a ListTerm
        ListTerm recipesTerm = (ListTerm) args[0];
        
        // Get the index as an integer.
        // (Assumes that the index is given as a number. Adjust if necessary.)
        int index = Integer.parseInt(args[1].toString());
        
        // If you want to use 1-indexing (as is more natural for users), adjust here:
        if (index < 1 || index > recipesTerm.size()) {
            // If the index is out of range, default to 1 (the first recipe)
            index = 1;
        }
        
        // Retrieve the recipe (lists in Jason use 0-indexing)
        Term recipe = recipesTerm.get(index - 1);
        
        // Unify the third argument (output variable) with the retrieved recipe
        return un.unifies(args[2], recipe);
    }
}
