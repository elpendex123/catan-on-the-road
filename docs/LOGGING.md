# Logging Configuration and Usage

This document describes the logging setup, log levels, and what information is logged at each level.

---

## Logging Setup

### Configuration File

**File:** `src/main/resources/logback-spring.xml`

**Features:**
- File appender only — no console output during normal operation
- Log file: `/tmp/catan.log`
- File overwrites on each application start (no rolling, no history)
- Root log level: TRACE (all levels logged to file)
- Log format includes: timestamp, thread, level, logger name, message

### Running the Game

```bash
./gradlew bootRun
```

Game runs silently (no console output). All activity logged to `/tmp/catan.log`.

### Viewing Logs

```bash
tail -f /tmp/catan.log
```

Or:

```bash
cat /tmp/catan.log | grep ERROR
```

---

## Log Levels and Usage

### TRACE

Very detailed execution flow tracking. Used for:
- Menu navigation (which option was selected)
- Back/Quit press detection
- Prompt display
- LineReader input/output

**Example:**
```
[TRACE] MenuPrompt: Player selected option [1]
[TRACE] MenuPrompt: Back button pressed, returning to previous menu
[TRACE] MenuPrompt: Displaying menu with 5 options
```

### DEBUG

State mutations and internal calculations. Used for:
- Deck operations (draw, discard, reshuffle)
- Hand changes (add resource, remove resource)
- Substitution ratio calculation
- Bonus tie-break evaluation
- Building row refill logic
- Event deck shuffling
- 5-of-a-kind reshuffle trigger

**Example:**
```
[DEBUG] ResourceDeck: Drew 3 cards, discard pile: 5 cards remaining in draw pile
[DEBUG] Hand (Alice): Added 2 brick, now total: 7 cards
[DEBUG] BuildingDeck: Row refilled after taking [Road], 4 cards drawn
[DEBUG] LongestRoute: Bob has 4 roads, strictly more than Alice's 3, transferring bonus
[DEBUG] Substitution: Alice has 3 roads, ratio 2:1
```

### INFO

High-level game events. Used for:
- Turn start (player name, round number)
- Build completed (what was built, who built it)
- Trade accepted/declined (summary of exchange)
- Event card drawn (which event, brief effect)
- VP changes (who gained VPs, current total)
- Bonus transfer (Longest Route/Largest Army transfer)
- Game won (who won, VP count, round number)
- Game quit (without winner)

**Example:**
```
[INFO] GameEngine: Alice's turn started, Round 1
[INFO] BuildAction: Alice built Road, cost: [1 brick, 1 wood]
[INFO] TradeAction: Alice <-> Bob: Alice gave [1 wood], Bob gave [1 ore]
[INFO] EventCard: Settlement build triggered Robber event
[INFO] Robber: Alice must discard 5 cards (12 > threshold 7)
[INFO] LongestRoute: Bob acquired Longest Route with 3 roads
[INFO] Game: Alice won with 7 VPs in Round 5
```

### WARN

Unusual but non-fatal conditions. Used for:
- Attempted action that was blocked (trade with player who has 0 of requested type, before auto-decline check)
- Defensive condition that shouldn't occur (e.g., metro all 4 already built, but player tries to build)
- Unexpected but recoverable state

**Example:**
```
[WARN] TradeAction: Bob attempted trade but no players have the requested resource
[WARN] EventResolver: Charity event triggered but all players have same VP (no givers/receivers)
```

### ERROR

Exceptions and fatal errors. Used for:
- Catch-all exception handler
- Stack traces for unexpected conditions
- Application-level errors

**Example:**
```
[ERROR] CatanOnTheRoadApplication: Uncaught exception
java.lang.IllegalStateException: Hand is empty but draw attempted
  at Hand.draw(Hand.java:45)
  at ...
```

---

## Example Game Log

Here's an excerpt from a complete game log:

```
[INFO] GameEngine: Alice's turn started, Round 1
[DEBUG] Harvest: Alice harvested 1 (base)
[DEBUG] Hand (Alice): Added 1 brick
[TRACE] MenuPrompt: Displayed turn menu, 6 options
[TRACE] MenuPrompt: Player selected [4] Build
[DEBUG] BuildAction: Checking affordability for Road
[INFO] BuildAction: Alice built Road, cost: [1 brick, 1 wood]
[DEBUG] BuildingDeck: Row refilled, now [Road, Settlement, Knight, City, Road]
[TRACE] MenuPrompt: Displayed build menu
[TRACE] MenuPrompt: Player selected [6] End Turn
[TRACE] MenuPrompt: Asking confirmation: End your turn?
[TRACE] MenuPrompt: Player selected [Y] Yes
[INFO] GameEngine: Alice's turn ended, Round 1
[INFO] GameEngine: Bob's turn started, Round 1
[DEBUG] Harvest: Bob harvested 1 (base) + 1 (city)
[DEBUG] Hand (Bob): Added 1 ore
[INFO] EventCard: Settlement build triggered event draw
[TRACE] EventDeck: Drew Robber event
[INFO] Robber: Checking discard thresholds for all players
[DEBUG] Robber: Alice (5 cards, 0 knights): threshold 7, no discard
[DEBUG] Robber: Bob (4 cards, 1 knight): threshold 8, no discard
[DEBUG] Robber: Carol (3 cards, 0 knights): threshold 7, no discard
[INFO] GameEngine: Event resolution complete
[INFO] GameEngine: Carol's turn started, Round 2
```

---

## Debugging with Logs

### Check for Specific Events

Find all builds:
```bash
grep "BuildAction" /tmp/catan.log
```

Find all trades:
```bash
grep "TradeAction" /tmp/catan.log
```

Find all event draws:
```bash
grep "EventCard:" /tmp/catan.log
```

Find all errors:
```bash
grep "ERROR" /tmp/catan.log
```

---

## Controlling Log Level at Runtime

To temporarily enable console output (for debugging), edit `logback-spring.xml` and change:

```xml
<root level="TRACE">
    <appender-ref ref="FILE" />
    <appender-ref ref="STDOUT" />  <!-- Add this -->
</root>
```

And add an appender:

```xml
<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
        <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
</appender>
```

Then rebuild and run. **Do not commit these changes.**

---

## Logger Names

Loggers are named after their class:

- `com.enrique.catanontheroad.shell.MainMenuCommands`
- `com.enrique.catanontheroad.shell.TurnMenu`
- `com.enrique.catanontheroad.shell.BuildMenu`
- `com.enrique.catanontheroad.game.GameEngine`
- `com.enrique.catanontheroad.game.action.BuildAction`
- `com.enrique.catanontheroad.game.action.TradeAction`
- `com.enrique.catanontheroad.game.bonus.LongestRoute`
- `com.enrique.catanontheroad.game.bonus.LargestArmy`
- `com.enrique.catanontheroad.game.event.EventResolver`
- `com.enrique.catanontheroad.game.deck.ResourceDeck`
- `com.enrique.catanontheroad.game.deck.BuildingDeck`
- `com.enrique.catanontheroad.game.deck.EventDeck`

To filter logs by logger:
```bash
grep "BuildAction" /tmp/catan.log
grep "LongestRoute" /tmp/catan.log
```

---

## Log File Size

The log file (`/tmp/catan.log`) is overwritten on each application start. For a typical game (12-15 rounds), expect:

- ~5-10 KB log file
- ~500-1000 log lines

No rolling or history is maintained (spec section 2.2).

---

## Testing and Logs

During test runs, each test writes to `/tmp/catan.log`. Logs are overwritten between test runs. To check logs from a specific test:

1. Run the test: `./gradlew test`
2. Check `/tmp/catan.log` immediately after
3. Note: If multiple tests run, the file contains only the last test's logs

For persistent test logs, copy the file before the next test run:
```bash
cp /tmp/catan.log /tmp/catan_test_backup.log
```
