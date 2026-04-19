# Changelog

## [Unreleased]

### Added
- Initial Gradle project setup with Spring Boot 3.4.1 and Spring Shell 3.4.0
- Resource, building, and event card domain model
- Seeded resource deck with discard reshuffle (61 cards)
- Building deck with row management and 5-of-a-kind reshuffle (34 cards)
- Event deck with Solstice reshuffle (7 cards)
- Metropolis stack for A/B sides (4 cards each)
- Player and hand management with VP calculation
- Longest Route tracking (first at 3+ roads, strictly-more transfer)
- Largest Army tracking (first at 2+ knights, strictly-more transfer)
- B-side metropolis tie-break abilities
- Turn engine with harvest and action phases
- Harvest phase with city/metropolis bonuses
- Build action with cost validation and placement rules
- Trade action with 1:1 swap and bonus card for acceptor
- Substitute action with road-based ratios (4:1 to 1:1)
- Win condition check (7+ VP on own turn)
- File-only logging to /tmp/catan.log
- ASCII art banner
- Event resolution for all 5 event types (Robber, Abundance, Charity, Solstice, Subsidy)
- Metropolis B upon-build draw effects (B-Road, B-Knight)
- GameService bridge between shell and game layers
- Spring Shell menus: main menu, setup, turn, build, trade, substitute
- ANSI color rendering for resources and UI elements
- Board renderer with building row and player areas
- Hand renderer with resource counts
- Scoreboard renderer with final scores
- Menu prompt helper with Back/Quit support
- Pass-device flow for hot-seat play
- Unit tests for all game components
- JaCoCo coverage configuration

### Implementation Status

**Complete:**
- Phase 1: Project Foundation
- Phase 2: Domain Model
- Phase 3: Player Model
- Phase 4: Bonus Tracking
- Phase 5: Game Engine Core
- Phase 6: Actions (Build, Trade, Substitute)
- Phase 7: Event Resolution
- Phase 8: Metropolis B upon-build effects
- Phase 9: Shell UI (menus, rendering, service layer)

**In Progress:**
- Phase 9-10: Shell UI (menus, rendering)

**Pending:**
- Phase 11-12: Integration tests, 100% coverage
