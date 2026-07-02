# Design and Implementation of Human–AI Cooperation in a Video Game

**Author:** Margarita H. Radeva    
**Maintained by:** Rafael C. Cardoso

---

## Overview
This repository contains a **Jason**–based **Symbolic AI** agent that integrates with the open‑source Overcooked-AI environment. The agent provides a transparent, logic‑driven teammate for real‑time human–AI collaboration in the Overcooked cooperative cooking benchmark.


---

## Prerequisites

Ensure you have the following installed:

- **Java**: OpenJDK 17 or higher

> **Note:** Additional dependencies (Python, Node, etc.) are required by the Overcooked-AI environment-refer to its README.

---

## Setup & Installation

1. **Build the Jason environment**:
   ```bash
   ./gradlew
   ```   
   **OR if using Windows**   
   ```bash
   cmd.exe /c gradlew.bat
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
