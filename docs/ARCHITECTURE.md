# Architecture

## Overview

The project follows a layered architecture with clear separation between game logic and UI:

```
┌─────────────────────────────────────────┐
│            Shell Layer                  │
│  (Spring Shell, menus, rendering)       │
├─────────────────────────────────────────┤
│           Service Layer                 │
│    (GameService - bridges UI to game)   │
├─────────────────────────────────────────┤
│            Game Layer                   │
│   (Pure Java - no Spring dependencies)  │
└─────────────────────────────────────────┘
```

## Package Structure

### `com.enrique.catanontheroad.game`

Pure Java game logic with **zero Spring annotations**. This allows:
- Easy unit testing without Spring context
- Potential extraction as a standalone library
- Clear domain boundaries

#### Key Classes

| Class | Responsibility |
|-------|----------------|
| `Game` | Root aggregate holding all game state |
| `GameEngine` | Orchestrates turn flow and phase transitions |
| `Player` | Player identity, hand, buildings, VP calculation |
| `Hand` | Resource card counting and payment |

#### Sub-packages

- `card/` - Card type definitions (ResourceType, BuildingCard, EventCard, Metropolis)
- `deck/` - Deck management (ResourceDeck, BuildingDeck, EventDeck, MetropolisStack)
- `action/` - Game actions (BuildAction, TradeAction, SubstituteAction)
- `bonus/` - Bonus tracking (LongestRoute, LargestArmy)
- `event/` - Event card resolution (EventResolver)
- `phase/` - Turn phases (HarvestPhase, ActionPhase)
- `rng/` - Seeded randomness (SeededRandom)

### `com.enrique.catanontheroad.service`

Bridge between shell and game layers.

| Class | Responsibility |
|-------|----------------|
| `GameService` | Holds current Game instance, action objects, and EventResolver |

### `com.enrique.catanontheroad.shell`

Spring Shell components for terminal UI.

| Class | Responsibility |
|-------|----------------|
| `MainMenuCommands` | Top-level Spring Shell command, game loop orchestration |
| `SetupMenu` | Player names, metropolis side, seed collection |
| `TurnMenu` | Action phase menu with view/build/trade/substitute/end |
| `BuildMenu` | Build submenu, metropolis picker, event card resolution |
| `TradeMenu` | 4-step trade flow with pass-device for partner |
| `SubstituteMenu` | Resource substitution with ratio display |

- `display/` - ANSI color helpers, board/hand/scoreboard renderers
- `input/` - Reusable menu prompt with Back/Quit, yes/no, pass-device support

### `com.enrique.catanontheroad.config`

Spring configuration.

| Class | Responsibility |
|-------|----------------|
| `ShellConfig` | Custom prompt provider |

## Data Flow

### Game Initialization
```
User input → SetupMenu → GameService.createGame() → new Game(players, side, seed)
                                                          ↓
                                                   Initialize decks
                                                   Deal starting hands
                                                   Set random starting player
```

### Turn Flow
```
GameEngine.executeHarvestPhase()
    ↓
All players draw 1 base card
Current player draws bonus cards (cities, metropolises)
    ↓
ActionPhase created (trade/substitute available)
    ↓
TurnMenu displayed → User selects action
    ↓
BuildAction/TradeAction/SubstituteAction executed
    ↓
Check win condition (7+ VP)
    ↓
GameEngine.endTurn() → advance to next player
```

### Building Flow
```
BuildAction.canAffordX(player)  → Check resources and prerequisites
                                  (e.g., city needs uncovered settlement)
    ↓
BuildAction.buildX(player, game) → Pay cost
                                   Take from building row
                                   Add to player area
                                   Update bonuses
                                   (Settlement triggers event card)
```

## Design Decisions

### Sealed Hierarchies
`BuildingCard` and `EventCard` use sealed interfaces with records:
```java
public sealed interface BuildingCard
    permits BuildingCard.Road, BuildingCard.Settlement,
            BuildingCard.City, BuildingCard.Knight {
    record Road() implements BuildingCard { ... }
    // ...
}
```

### Immutable Records
Card types, results, and validation outcomes use records for immutability:
```java
public record BuildResult(boolean success, String message) { }
public record TradeOffer(Player offerer, List<ResourceType> offeredTypes, ...) { }
```

### Seeded Randomness
All randomness flows through `SeededRandom`, enabling:
- Deterministic testing with fixed seeds
- Reproducible games for debugging

### Covered Buildings
Cities cover settlements, metropolises cover cities:
```java
// In Player.java
private int uncoveredSettlementCount;
private int coveredSettlementCount;  // Under cities, don't count for VP
```

## Testing Strategy

- **Unit tests**: Test `game/` package in isolation (no Spring context)
- **Integration tests**: Test shell layer with scripted input
- **Coverage**: JaCoCo enforces 100% line/branch coverage

## Future Considerations

- Save/load could serialize `Game` to JSON
- AI opponent would implement strategy in `game/` layer
- Web UI would replace `shell/` layer
