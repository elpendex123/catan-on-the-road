# Glossary of Terms

This document defines game terms, mechanical terms, and technical terms used throughout the project.

---

## Game Mechanics

### Building

A piece placed in a player's area. Buildings contribute to VPs and game effects. Types:
- **Road** — 0 VP, enables movement and road bonuses
- **Settlement** — 1 VP, upgraded to city or metropolis
- **City** — 2 VP, covers a settlement, generates harvest
- **Knight** — 0 VP, contributes to Largest Army bonus
- **Metropolis** — 3 VP, covers a city, has special effects

### Uncovered Building

A building that is visible and active in a player's area. Contributes to VP, harvest, and bonus tracking.

Compare: **Covered building** — a building hidden beneath an upgraded piece. Example: a settlement covered by a city contributes 0 VP.

### Resource Card

One of 5 types: brick, wood, wool, wheat, ore. Used to pay building costs and affected by events. Held in a player's hand.

### Hand

The set of resource cards a player currently owns. Limited only by deck size in practice. Affected by harvest, trade, substitution, and events.

### Resource Deck

The deck of 61 resource cards. Players draw during harvest. When empty, discard pile reshuffles into it.

### Building Deck

The deck of 34 building cards (roads, settlements, cities, knights). The top 5 cards form the **building row**. When a card is taken, the row refills from the deck. When the deck is empty, discard pile reshuffles.

### Building Row

A face-up display of 5 building cards. Players can build from the row or use the generic "build" action and pay cost to draw from the deck. The row is guaranteed to contain at least one of each type at game start and after any 5-of-a-kind reshuffle.

### Event Card

One of 7 event cards that can be drawn when a player builds a settlement. Types: Robber, Abundance, Charity, Solstice, Subsidy.

### Event Deck

The deck of 7 event cards. When empty without a Solstice being drawn, it reshuffles from discard. Solstice explicitly reshuffles the deck.

---

## Victory and Bonuses

### Victory Points (VP)

The score metric. First player to reach 7 VPs wins the game. A player's VP total is:
- 1 per uncovered settlement
- 2 per uncovered city
- 3 per uncovered metropolis
- 2 if holding Longest Route bonus
- 2 if holding Largest Army bonus

### Longest Route

A bonus card worth 2 VPs. Held by the player with the most roads (minimum 3). Transfers to another player if they have strictly more roads. In A-side games, ties keep the current holder. In B-side, ties are won by the holder of the B-Longest-Route metropolis.

### Largest Army

A bonus card worth 2 VPs. Held by the player with the most knights (minimum 2). Transfers on strictly more. Ties behave like Longest Route (A-side keeper, B-side tiebreak).

### Metropolis Side

A game-wide choice at setup:
- **A-side** — All 4 metropolises are identical. Cost: 3 wool + 1 ore. Effect: +2 harvest resources per uncovered metropolis. No unique abilities, no tie-break.
- **B-side** — 4 unique metropolises, each with a special ability. Cost: 3 wool + 1 ore each. Effect: +1 harvest resource per uncovered metropolis. Each has a unique upon-build or tie-break effect.

### Metropolis (A-side)

Four identical cards. Each: +2 harvest draw, worth 3 VP. No other effects.

### Metropolis B-Road

Unique B-side metropolis. Upon build: draw 1 resource per road the player has built. +1 harvest draw. Worth 3 VP.

### Metropolis B-Longest-Route

Unique B-side metropolis. Holder wins all ties for Longest Route bonus (immediate effect on tie situations). +1 harvest draw. Worth 3 VP.

### Metropolis B-Knight

Unique B-side metropolis. Upon build: draw 1 resource per knight the player has built. +1 harvest draw. Worth 3 VP.

### Metropolis B-Largest-Army

Unique B-side metropolis. Holder wins all ties for Largest Army bonus (immediate effect on tie situations). +1 harvest draw. Worth 3 VP.

---

## Turns and Phases

### Turn

A complete cycle of one player's actions, divided into two phases:
1. **Harvest phase** (automatic)
2. **Action phase** (menu-driven)

After the action phase, play passes to the next player (clockwise).

### Harvest Phase

Automatic phase at the start of each turn:
- Current player draws 1 base resource
- +1 per uncovered city
- +2 per uncovered A metropolis
- +1 per uncovered B metropolis
- Other players draw 1 base only (no bonuses)

### Action Phase

Player-driven phase where the current player can:
- Trade (once per turn)
- Substitute (once per turn)
- Build (unlimited)
- View board/hand (no turn effect)
- End turn
- Quit

### Round

A complete cycle where all players have taken one turn. Round 1 starts with player 1, Round 2 with player 1 again (after player 3 ends their turn).

---

## Actions and Effects

### Trade

Exchange exactly 1 resource card with another player. Both sides must have exactly 1 card. Types differ. If the target has 0 of the requested type, trade is auto-declined. The target draws 1 bonus resource if they accept.

### Substitute

Exchange N resource cards of one type for 1 card of another type. The ratio N depends on roads:
- 1 road → 4:1
- 2 roads → 3:1
- 3 roads → 2:1
- 4+ roads → 1:1

### Build

Pay a cost in resources to place a building in the player's area. Building row refills after each build.

### Robber Event

Drawn on settlement build. Each player with more than `7 + knight count` resources must discard half (rounded down).

### Abundance Event

Drawn on settlement build. Each player without Longest Route or Largest Army draws 2 resources.

### Charity Event

Drawn on settlement build. Players with the most VPs give 1 resource each to players with the fewest VPs. Has 5 distinct sub-cases (A, B, C, D) depending on how many players tie for top/bottom.

### Solstice Event

Drawn on settlement build. Each player draws 1 resource. All 7 event cards are reshuffled into the event deck.

### Subsidy Event

Drawn on settlement build. Each player draws 1 resource per uncovered settlement they have.

---

## Game State

### Initial State

Game starts with:
- 3 players (names entered at setup)
- Each player has 1 starting settlement and 1 starting road
- Each player has 2 starting resource cards (drawn randomly)
- Building row is initialized with guaranteed mix of all 4 types
- Metropolis stack is placed on chosen side
- Longest Route and Largest Army have no holder
- All decks shuffled with seeded RNG

### Win Condition

Checked only on the current player's own turn (action phase). If current player's VP ≥ 7, game ends immediately. That player wins.

### Tie-Breaking (A vs B Metropolis Side)

**A-side:**
- Longest Route and Largest Army ties: current holder retains bonus
- No metropolis tie-break ability

**B-side:**
- B-Longest-Route metropolis holder wins ties for Longest Route
- B-Largest-Army metropolis holder wins ties for Largest Army
- Tie-break is immediate when the metropolis is built or when a new tie occurs

---

## Technical Terms

### Seeded RNG

A `SeededRandom` class providing deterministic randomness. Same seed always produces the same sequence of random events. Used throughout for deck shuffling, event selection, etc. Enables reproducible games.

### Deck Reshuffle

When a deck runs out of cards, the discard pile is shuffled into a new deck and drawing continues. Applies to:
- Resource deck: always reshuffles
- Building deck: reshuffles if empty
- Event deck: reshuffles if empty (or explicitly by Solstice)

### 5-of-a-Kind Reshuffle

A special reshuffle in the building row: if all 5 visible cards are the same type, all 5 are shuffled back into the building deck and a new row of 5 is dealt. Ensures row diversity.

### Coverage Exclusion

A class marked as excluded from JaCoCo coverage verification. Used for shell UI (tested via integration tests) and defensive code (unreachable in 3-player games). Listed in `build.gradle`.

### Defensive Code

Code that handles conditions that cannot theoretically occur given the game rules. Example: the IMPOSSIBLE charity case in a 3-player game. Included for robustness but excluded from coverage.

### Hot-Seat

A game mode where multiple players share a single device, passing it between turns. Each turn transition includes a "pass device" prompt.

### Menu-Driven

An input mode where the player navigates through numbered/lettered menus rather than typing free-form commands (REPL style).

### Integration Test

A test that instantiates Spring context and tests multiple layers together (menu navigation, game state changes, persistence). Uses `StubLineReader` to simulate user input.

### Unit Test

A test that exercises a single class or small group of classes in isolation, without Spring context. Typical for game logic (`game/` package).

### JaCoCo

Java Code Coverage library. Enforces 100% line and branch coverage (with exclusions). Generates HTML reports in `build/reports/jacoco/test/html/`.

### Spring Shell

A Spring Framework wrapper around JLine that simplifies building CLI applications with commands and shell-like behavior. Used for the menu system.

### ANSI Color

Terminal color codes (e.g., `\033[31m` for red). Used to color-code resources, error messages, and UI elements.

### REPL

Read-Eval-Print Loop. A command interpreter that reads one command at a time. Contrast with menu-driven (no REPL for this project).

---

## Abbreviations

- **VP** — Victory Points
- **RNG** — Random Number Generator
- **ANSI** — American National Standards Institute (for terminal colors)
- **JDBC** — Java Database Connectivity (not used in this project)
- **LOC** — Lines of Code
- **CLI** — Command-Line Interface
- **DOM** — (Not used; old Gradle jargon)
