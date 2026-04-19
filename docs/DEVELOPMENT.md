# Development Guide

## Prerequisites

- Java 17+
- Git

No Gradle installation needed - the wrapper is included.

## Setup

```bash
git clone git@github.com:elpendex123/catan-on-the-road.git
cd catan-on-the-road
./gradlew build
```

## Common Commands

```bash
# Build
./gradlew build

# Run the game
./gradlew bootRun

# Run tests
./gradlew test

# Run tests with coverage verification
./gradlew check

# View coverage report
open build/reports/jacoco/test/html/index.html

# Clean build
./gradlew clean build
```

## Project Layout

```
catan_on_the_road/
├── build.gradle          # Build configuration
├── settings.gradle       # Project name
├── gradlew, gradlew.bat  # Gradle wrapper
├── src/
│   ├── main/
│   │   ├── java/         # Source code
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── banner.txt
│   │       └── logback-spring.xml
│   └── test/java/        # Tests
├── docs/                 # Documentation
└── README.md
```

## Adding Features

### Adding a new action

1. Create action class in `game/action/`:
```java
public class NewAction {
    public record Result(boolean success, String message) { }
    public Result execute(Player player, Game game) { ... }
}
```

2. Add tests in `test/.../game/action/NewActionTest.java`

3. Integrate with `GameEngine` if needed

4. Add menu option in shell layer

### Adding a new event card

1. Add record to `EventCard.java`:
```java
public sealed interface EventCard permits ..., EventCard.NewEvent {
    record NewEvent() implements EventCard {
        @Override public String name() { return "New Event"; }
    }
}
```

2. Add to `EventDeck` initialization

3. Create handler in event resolution

4. Add tests

## Code Style

- Pure Java in `game/` package (no Spring annotations)
- Use records for immutable data
- Use sealed interfaces for closed hierarchies
- AssertJ for test assertions
- Method naming: `should_X_when_Y` or `given_X_when_Y_then_Z`

## Testing

Tests are organized to mirror the source structure:
```
src/test/java/com/enrique/catanontheroad/
├── game/
│   ├── GameTest.java
│   ├── HandTest.java
│   ├── PlayerTest.java
│   ├── card/
│   ├── deck/
│   ├── action/
│   └── ...
└── config/
    └── ShellConfigTest.java
```

### Writing Tests

```java
@Test
void should_add_road_when_player_has_resources() {
    // Given
    player.getHand().add(ResourceType.BRICK);
    player.getHand().add(ResourceType.WOOD);

    // When
    var result = buildAction.buildRoad(player, game);

    // Then
    assertThat(result.success()).isTrue();
    assertThat(player.getRoadCount()).isEqualTo(2);
}
```

## Logging

Logs go to `/tmp/catan.log`. Levels used:
- `TRACE`: Menu navigation
- `DEBUG`: State mutations
- `INFO`: Game events (turn start, builds, trades)
- `WARN`: Blocked actions
- `ERROR`: Exceptions

Watch logs during development:
```bash
tail -f /tmp/catan.log
```

## Coverage

JaCoCo enforces 100% line and branch coverage. Excluded:
- `CatanOnTheRoadApplication.java` (Spring Boot main class)

Run coverage check:
```bash
./gradlew jacocoTestCoverageVerification
```
