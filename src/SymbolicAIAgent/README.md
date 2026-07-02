# Design and Implementation of Human–AI Cooperation in a Video Game

**Author:** Margarita H. Radeva    
**Maintained by:** Rafael C. Cardoso

---

## Overview
This repository contains a **Jason 3.2.0**–based **Symbolic AI** agent that integrates with the open‑source Overcooked-AI environment. The agent provides a transparent, logic‑driven teammate for real‑time human–AI collaboration in the Overcooked cooperative cooking benchmark.

> **Note:** This is one of two repositories required to run the full system. You will also need the Overcooked-AI environment code (see **Related Repos** below).

---

## Links & Related Repos

- **Overcooked-AI (core environment):**
  https://github.com/margaritaradeva/OvercookedAI.git
- **Original open-source repository (not needed):**
  https://github.com/HumanCompatibleAI/overcooked_ai

---

## Prerequisites

Ensure you have the following installed:

- **Jason**: v3.2.0 
- **Java**: OpenJDK 17  (do **not** use a higher version)
- **Git Bash**  (set as your default terminal, e.g. in VS Code, if using Windows)
- **Gradle** - You do **NOT** need to install it as it is already contained within the repository

> **Note:** Additional dependencies (Python, Node, etc.) are required by the Overcooked-AI environment-refer to its README.

---

## Setup & Installation

1. **Clone this repository**:
   ```bash
   git clone https://github.com/margaritaradeva/SymbolicAIAgent
   cd SymbolicAIAgent
   ```
2. **Build the Jason environment**:
   ```bash
   gradle build
   ```
3. **Run the Jason symbolic AI agent**:
   ```bash
   jason kitchen.mas2j         
   ```

---

## Usage
1. Start the Overcooked-AI environment from the related repo and create a game.
2. Execute the Jason agent as above-the agent will join automatically as the second player.
3. Play alongside the agent and observe its real-time cooperation.

---

## Repository Structure
```plaintext
SymbolicAIAgent/
├── build/                                # Compiled classes and generated files
├── jia/                                  # Java internal actions
│   ├── get_recipe_at_index.java          # Get a recipe at a certain index     
│   └── get_pot.java                      # Check if an ingredient matches any pot
├── staychef.asl                          # Agent logic written in AgentSpeak
├── build.gradle                          # Gradle build script
├── Kitchen.java                          # Agent Environment
├── kitchen.mas2j                         # Configuration file               
├── settings.gradle                       # Gradle settings
└── README.md                             # You are here!
```

---

## Acknowledgements

- **HumanCompatibleAI/overcooked_ai** for the original cooperative benchmark environment.

---
