# Future Enhancements (Out of Scope)

This document lists potential features and improvements that are not part of the current 1.0 release but may be added in future versions.

---

## Save and Load

**Scope:** Currently no persistence. Game state lives only in JVM memory.

**Planned for future:**
- Save game to JSON file (`~/.catan/game_save_1.json`)
- Load saved game from main menu
- Multiple save slots
- Save metadata (player names, date, round number)
- Resume interrupted games

**Architecture notes:**
- `Game` and all game objects are JSON-serializable (Jackson configured)
- Would require UI layer changes (new menu option "Load Game")
- Would need to handle incompatible saves across versions

---

## 4-Player Mode

**Scope:** Currently 3 players only (hardcoded).

**Planned for future:**
- Allow player count selection at setup: 2, 3, or 4 players
- Adjust starting buildings per CATAN rulebook (fewer settlements for 2-player)
- Re-test all game logic with 4 players:
  - Charity event IMPOSSIBLE case (now possible with 4 players)
  - Road/knight counting with more competition
  - Metropolis availability with longer play
  - Expected game length (may be 15+ rounds)

**Architecture notes:**
- Change setup prompts to ask for player count
- Remove hardcoded `List.of("Alice", "Bob", "Carol")` patterns
- Update tests to include 4-player scenarios

---

## AI Opponent

**Scope:** Currently all players are human, hot-seat only.

**Planned for future:**
- Single-player mode: human vs 2 or 3 AI players
- AI decision-making:
  - Simple heuristic: maximize VP, then build roads, then build settlements
  - Intermediate: consider trade opportunities, block opponents
  - Advanced: Monte Carlo tree search for optimal move selection
- AI configurable difficulty

**Architecture notes:**
- Create `AIPlayer` interface with `decideAction()` method
- Wrap current menu-driven actions in a logic layer
- Add delays to AI turns for readability

---

## Networking / Multi-Device Play

**Scope:** Currently single-device hot-seat only.

**Planned for future:**
- WebSocket server for remote players
- Web client (HTML/CSS/JS) in addition to CLI
- Player names resolved to remote addresses
- Network protocol: JSON messages for game events

**Architecture notes:**
- Separate game engine from UI entirely
- Create an event-driven architecture (game publishes events, UI subscribes)
- Server maintains game state, clients sync via events

---

## Web UI

**Scope:** Currently CLI only via Spring Shell.

**Planned for future:**
- Web UI built with React or Vue.js
- Visual board representation (not ASCII art)
- Drag-and-drop building placement
- Real-time multiplayer via WebSocket
- Mobile responsive design

**Architecture notes:**
- Game engine (`game/` package) remains unchanged
- Service layer exposes REST API (`/api/game/`, `/api/actions/`)
- Web frontend is a separate repository

---

## Undo / Rewind

**Scope:** Currently no undo within a turn.

**Planned for future:**
- Undo last action within action phase
- Full rewind to previous turn (with confirmation)
- Move history log showing all actions taken

**Architecture notes:**
- Game engine maintains action log (immutable)
- Undo rolls back state from log
- Performance: could be expensive for deep rewinds

---

## Replay and Spectator Mode

**Scope:** No replay capability.

**Planned for future:**
- Save/replay game with exact same random seed
- Speed control (slow, normal, fast)
- Jump to specific turn or player
- Spectator mode to watch live games

**Architecture notes:**
- Seed + action log fully determine game outcome
- Replay engine reconstructs game state from log

---

## Advanced Statistics

**Scope:** No game statistics collected.

**Planned for future:**
- Track win/loss history across multiple games
- Statistics: average game length, VP per player, most-built building type
- Leaderboard (if multi-device)
- Replay analysis (bottleneck phases, longest turns)

**Architecture notes:**
- Extend game history to include metadata
- Time each action to identify slow turns

---

## Configurable Rules

**Scope:** CLAUDE.md rules are hardcoded.

**Planned for future:**
- House rules toggle:
  - Charity event optional
  - Free first build
  - Custom starting resources
  - Victory point threshold (7, 10, 12, etc.)
  - Metropolis side chosen per metropolis (mix A and B)

**Architecture notes:**
- Create `GameConfig` class with rule variants
- Conditionally apply rules in game engine
- Test all variant combinations

---

## Improved AI and Strategy Guide

**Scope:** No strategy hints.

**Planned for future:**
- In-game hints: "You could build a road to get Longest Route"
- Strategy guide: trade recommendations, board position analysis
- AI behavior modes: aggressive, defensive, balanced

**Architecture notes:**
- Create strategy evaluator class
- Rank available actions by expected value

---

## Docker Packaging

**Scope:** No Docker image.

**Planned for future:**
- Dockerfile for containerized deployment
- Pre-built JAR in image
- Simplifies distribution and multi-platform support

**Dockerfile outline:**
```dockerfile
FROM openjdk:17-slim
COPY build/libs/catan*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

---

## Performance Optimization

**Scope:** No profiling or optimization beyond basic design.

**Planned for future:**
- Profile game engine for hotspots
- Optimize deck shuffling with lazy evaluation
- Cache VP calculation (currently O(1) anyway, so minimal benefit)

**Architecture notes:**
- Unlikely to be needed for this game (simple state, small decks)
- Would revisit if game scales to 10+ players or 100+ cards

---

## Accessibility

**Scope:** Terminal-based UI assumes sighted users.

**Planned for future:**
- Screen reader support (ARIA annotations for web UI)
- High-contrast mode
- Configurable font size
- Keyboard-only navigation (already have this for CLI)

---

## Internationalization (i18n)

**Scope:** English only.

**Planned for future:**
- Resource bundles for other languages
- Locale-based formatting (numbers, dates)
- Translated menu text and game terminology

**Architecture notes:**
- Extract all strings to `messages.properties`
- Use `ResourceBundle` for runtime lookup

---

## Bug Tracker and Issue Management

**Scope:** No formal bug tracking.

**Future workflow:**
- GitHub Issues for bugs and feature requests
- Pull request reviews before merge
- Semantic versioning (1.0.0, 1.1.0, 2.0.0, etc.)
- Release notes per version

---

## Documentation

**Scope:** Comprehensive docs already in `docs/`.

**Future improvements:**
- API documentation (Javadoc comments on public classes)
- Video tutorial (walkthrough of first game)
- Beginner's guide separate from rules reference
- Architecture diagrams (ASCII art or exported images)

---

## Continuous Integration

**Scope:** No CI/CD pipeline.

**Planned for future:**
- GitHub Actions to run tests on every push
- Automated coverage reports
- Build status badge in README
- Automated releases to GitHub

---

## Known Limitations

These limitations are accepted in the current design:

1. **No branching in menus** — Every menu is linear (Back/Quit only). No nested menus within nested menus.
2. **Single game instance** — Only one game can be active at a time. No simultaneous games.
3. **No persistent state** — Game is lost if process exits.
4. **No input validation** — Player names can be empty after input (although prompts reject empty input during entry).
5. **Fixed 3-player count** — Hardcoded, cannot change without recompile.
6. **No graphics** — ASCII art only, no sprite rendering.
7. **No sound** — Silent except for logged warnings.
8. **No undo** — Actions are final once taken.
9. **No cheating prevention** — Hot-seat mode trusts players not to peek.
10. **Single-device only** — No network support.

---

## Success Criteria for Major Future Features

### Save/Load
- [ ] Can save a game mid-play
- [ ] Can load and resume the exact same game
- [ ] Metadata preserved (player names, seed, round number)
- [ ] Multiple save slots supported
- [ ] Load menu displays saves with metadata

### 4-Player
- [ ] All game logic updated and tested with 4 players
- [ ] Setup menu allows selecting 2, 3, or 4 players
- [ ] IMPOSSIBLE charity case is properly tested
- [ ] Game length reasonable (under 20 rounds typical)

### Web UI
- [ ] React or Vue.js frontend deployed
- [ ] Visual board representation renders correctly
- [ ] All menus replicated in web UI
- [ ] Mobile responsive on tablets
- [ ] Web and CLI backends use same game engine

### Network
- [ ] WebSocket server handles 4+ concurrent games
- [ ] Players in different locations can play together
- [ ] Game state stays in sync across clients
- [ ] Latency under 1 second typical

### AI
- [ ] AI players make legal moves
- [ ] AI can win against human players
- [ ] Difficulty levels show meaningful difference in strategy
- [ ] AI moves are deterministic given game state
