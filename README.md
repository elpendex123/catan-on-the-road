# CATAN: On the Road

A menu-driven, text-based, 3-player hot-seat terminal implementation of the CATAN: On the Road card game.

## Overview

This is a single-process terminal game where 3 players take turns on the same device. The first player to reach 7 visible victory points on their own turn wins.

## Features

- 3-player hot-seat gameplay
- Menu-driven interface with ANSI colors
- Seeded random number generator for deterministic games
- Two metropolis sides (A and B) with different mechanics
- Full implementation of game rules including:
  - Resource harvesting based on buildings
  - Building roads, settlements, cities, knights, and metropolises
  - Trading with other players (1:1 swap + bonus card)
  - Resource substitution with road-based ratios
  - Longest Route and Largest Army bonuses
  - Event cards (Robber, Abundance, Charity, Solstice, Subsidy)

## Requirements

- Java 17 or higher
- No other dependencies (Gradle wrapper included)

## Build & Run

```bash
# Build the project
./gradlew build

# Run the game
./gradlew bootRun

# Run tests
./gradlew test

# View test coverage report
open build/reports/jacoco/test/html/index.html
```

## How to Play

### Setup
1. Enter names for 3 players
2. Choose metropolis side (A or B)
3. Optionally enter a seed for deterministic gameplay

### Turn Structure
Each turn has two phases:
1. **Harvest Phase** (automatic): All players draw 1 resource card. Current player draws bonus cards for cities (+1 each) and metropolises (+2 for A, +1 for B).
2. **Action Phase**: Current player can:
   - **Build** (unlimited): Roads, settlements, cities, knights, metropolises
   - **Trade** (once per turn): Swap 1 resource with another player
   - **Substitute** (once per turn): Convert resources at road-based ratio
   - **View board/hand**: Check game state
   - **End turn**: Pass to next player

### Victory Points
- Settlement: 1 VP
- City: 2 VP
- Metropolis: 3 VP
- Longest Route (3+ roads): 2 VP
- Largest Army (2+ knights): 2 VP

First to 7 VP on their own turn wins!

### Building Costs
| Building | Cost |
|----------|------|
| Road | 1 brick + 1 wood |
| Settlement | 1 brick + 1 wood + 1 wool + 1 wheat |
| City | 2 wheat + 3 ore |
| Knight | 1 wool + 1 ore |
| Metropolis | 3 wool + 1 ore |

### Substitution Ratios
| Roads | Ratio |
|-------|-------|
| 1 | 4:1 |
| 2 | 3:1 |
| 3 | 2:1 |
| 4+ | 1:1 |

## Project Structure

```
src/main/java/com/enrique/catanontheroad/
├── CatanOnTheRoadApplication.java    # Spring Boot entry point
├── config/
│   └── ShellConfig.java              # Spring Shell configuration
├── game/                             # Pure game logic (no Spring)
│   ├── Game.java                     # Root game state
│   ├── GameEngine.java               # Turn orchestration
│   ├── Player.java                   # Player state and VP
│   ├── Hand.java                     # Resource card management
│   ├── card/                         # Card types
│   │   ├── ResourceType.java
│   │   ├── BuildingCard.java
│   │   ├── EventCard.java
│   │   ├── Metropolis.java
│   │   └── ...
│   ├── deck/                         # Deck management
│   │   ├── ResourceDeck.java
│   │   ├── BuildingDeck.java
│   │   ├── EventDeck.java
│   │   └── MetropolisStack.java
│   ├── action/                       # Game actions
│   │   ├── BuildAction.java
│   │   ├── TradeAction.java
│   │   └── SubstituteAction.java
│   ├── bonus/                        # Bonus tracking
│   │   ├── LongestRoute.java
│   │   └── LargestArmy.java
│   ├── event/                        # Event card resolution
│   │   └── EventResolver.java
│   ├── phase/                        # Turn phases
│   │   ├── HarvestPhase.java
│   │   └── ActionPhase.java
│   └── rng/
│       └── SeededRandom.java
├── service/
│   └── GameService.java              # Bridge to shell layer
└── shell/                            # UI layer (Spring Shell)
    ├── MainMenuCommands.java         # Top-level shell command
    ├── SetupMenu.java                # Game setup flow
    ├── TurnMenu.java                 # Action phase menu
    ├── BuildMenu.java                # Build submenu with event resolution
    ├── TradeMenu.java                # Trade flow with pass-device
    ├── SubstituteMenu.java           # Substitute flow
    ├── display/
    │   ├── AnsiColors.java           # ANSI escape helpers
    │   ├── BoardRenderer.java        # Building row and player areas
    │   ├── HandRenderer.java         # Resource hand display
    │   ├── PlayerAreaRenderer.java   # Turn header with stats
    │   └── ScoreboardRenderer.java   # Final scoreboard
    └── input/
        └── MenuPrompt.java           # Reusable menu helper
```

## Logging

Logs are written to `/tmp/catan.log` (file only, no console output).

```bash
# Watch logs during gameplay
tail -f /tmp/catan.log
```

## Architecture

The `game/` package is pure Java with no Spring dependencies, making it easy to test and potentially extract as a library. The `shell/` and `service/` packages handle the terminal UI using Spring Shell.

See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for details.

## Documentation

- [Architecture](docs/ARCHITECTURE.md) - Package design and data flow
- [Rules Reference](docs/RULES_REFERENCE.md) - Complete game rules
- [Development](docs/DEVELOPMENT.md) - Setup and contribution guide
- [Changelog](docs/CHANGELOG.md) - Version history

## License

MIT
