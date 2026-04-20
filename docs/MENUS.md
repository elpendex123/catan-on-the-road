# Complete Menu Tree Reference

This document is a reference for every menu, option, and prompt in the game. It is used to verify UI correctness and test coverage.

---

## Top Menu (Pre-Game)

**Menu:** Main menu at startup.

**Display:**
```
=== CATAN: On the Road ===
[1] New game
[Q] Quit
```

**Options:**
- `[1] New game` → Begin setup flow (SetupMenu)
- `[Q] Quit` → Exit application

---

## Setup Flow (Sequential)

The setup flow consists of 5 sequential prompts. Each step (except the first) has **[B] Back** to return to the previous step. All steps have **[Q] Quit** to exit.

### Step 1: Player 1 Name

**Prompt:**
```
Enter name for Player 1:
```

**Input:** Free text, any non-empty string (no length limit enforced in spec).

**Validation:** Cannot be empty.

**Navigation:**
- Enter name → Step 2
- `[Q]` → Exit app, show final scoreboard (empty game)

**Note:** No Back button on first step.

---

### Step 2: Player 2 Name

**Prompt:**
```
Enter name for Player 2:   [B] Back   [Q] Quit
```

**Input:** Free text.

**Validation:** Cannot be empty.

**Navigation:**
- Enter name → Step 3
- `[B]` → Step 1
- `[Q]` → Exit app, show final scoreboard

---

### Step 3: Player 3 Name

**Prompt:**
```
Enter name for Player 3:   [B] Back   [Q] Quit
```

**Input:** Free text.

**Validation:** Cannot be empty.

**Navigation:**
- Enter name → Step 4
- `[B]` → Step 2
- `[Q]` → Exit app, show final scoreboard

---

### Step 4: Metropolis Side Selection

**Display:**
```
Metropolis side for this game?
[1] A side — all metropolises give +2 harvest draws, no special abilities
[2] B side — all metropolises give +1 harvest draw plus a unique ability per card
[B] Back
[Q] Quit
```

**Options:**
- `[1]` or `[2]` → Step 5
- `[B]` → Step 3
- `[Q]` → Exit app

---

### Step 5: Seed Entry

**Prompt:**
```
Enter seed (blank for random):   [B] Back   [Q] Quit
```

**Input:** Optional integer, or blank for auto-generated seed.

**Validation:** If entered, must be a valid integer (no validation on blank).

**Navigation:**
- Enter seed (or blank) → Initialize game, show summary, start Round 1 (TurnMenu)
- `[B]` → Step 4
- `[Q]` → Exit app

---

### Setup Summary (Before Turn 1)

After setup completes, a summary is displayed before the first harvest phase:

**Display:**
```
════════════════════════════════════════════════════════════
Game initialized.
Players (in turn order):
  1. Alice
  2. Bob
  3. Carol
Metropolis side: A
Seed: 42
════════════════════════════════════════════════════════════

Press ENTER to begin...
```

---

## Turn Menu (Action Phase)

Shown after automatic harvest phase. This is the main in-game menu where players take actions.

**Header:**
```
════════════════════════════════════════════════════════════
=== ALICE's turn — Round 4 ===
VPs: 3    Hand size: 5
Roads: 2    Settlements: 1    Cities: 1    Knights: 0    Metropolises: 0
Longest Route: BOB (3 roads)    Largest Army: — (no holder)
════════════════════════════════════════════════════════════
```

**Options:**
```
[1] View board
[2] View my hand
[3] Trade with another player              (already used this turn) [if trade used]
[4] Build
[5] Substitute resources                   (already used this turn) [if substitute used]
[6] End turn
[Q] Quit & show final scores
```

**Navigation:**
- `[1]` → View Board menu
- `[2]` → View Hand menu
- `[3]` → Trade Flow (if not used) or red error message (if already used)
- `[4]` → Build menu
- `[5]` → Substitute menu (if not used) or error (if used)
- `[6]` → End Turn Confirmation
- `[Q]` → Final Scoreboard, exit

---

## View Board Menu

**Display:** ASCII boxes showing building row and 3 player areas.

```
╔═══════════════════════════ BUILDING ROW ═══════════════════════════╗
║                                                                    ║
║  [1] ROAD           [2] SETTLEMENT    [3] CITY                     ║
║  1 brick            1 brick           2 wheat                      ║
║  1 wood             1 wood            3 ore                        ║
║                     1 wool                                         ║
║                     1 wheat                                        ║
║                                                                    ║
║  [4] KNIGHT         [5] ROAD                                       ║
║  1 wool             1 brick                                        ║
║  1 ore              1 wood                                         ║
║                                                                    ║
╚════════════════════════════════════════════════════════════════════╝

╔══ ALICE (3 VP) ══╗  ╔══ BOB (5 VP) ════╗  ╔═ CAROL (2 VP) ═╗
║ Roads:        2  ║  ║ Roads:        3 *║  ║ Roads:      1  ║
║ Settlements:  1  ║  ║ Settlements:  2  ║  ║ Settlements:2  ║
║ Cities:       1  ║  ║ Cities:       0  ║  ║ Cities:     0  ║
║ Knights:      0  ║  ║ Knights:      2 +║  ║ Knights:    0  ║
║ Metropolises: 0  ║  ║ Metropolises: 0  ║  ║ Metropolises:0 ║
║ Hand size:    5  ║  ║ Hand size:    8  ║  ║ Hand size:  3  ║
╚══════════════════╝  ╚══════════════════╝  ╚════════════════╝

* = Longest Route    + = Largest Army

[B] Back   [Q] Quit
```

**Navigation:**
- `[B]` → Back to Turn Menu
- `[Q]` → Final Scoreboard, exit

---

## View Hand Menu

**Display:**
```
=== ALICE's hand ===
  1. brick  x2
  2. wood   x1
  3. wool   x0
  4. wheat  x3
  5. ore    x1

Total: 7 cards

[B] Back   [Q] Quit
```

Resource names are colored per ANSI palette.

**Navigation:**
- `[B]` → Back to Turn Menu
- `[Q]` → Final Scoreboard, exit

---

## Build Menu

**Display:**
```
=== Build ===
[1] Road          (1 brick, 1 wood)                     [affordable]
[2] Settlement    (1 brick, 1 wood, 1 wool, 1 wheat)    [not affordable — need 1 more wool]
[3] City          (2 wheat, 3 ore)                      [need uncovered settlement]
[4] Knight        (1 wool, 1 ore)                       [affordable]
[5] Metropolis    (3 wool, 1 ore)                       [need uncovered city]
[6] Build a specific card from the building row
[B] Back   [Q] Quit
```

**Options:**
- `[1]` through `[5]` → Check legality and affordability. If valid, execute build. If invalid, show error and re-prompt.
- `[6]` → Show building row with numbered positions. Player selects a position, then the same legality/affordability check occurs.
- `[B]` → Back to Turn Menu
- `[Q]` → Final Scoreboard, exit

**Note:** After building a settlement, an event card is immediately drawn and resolved before returning to the build menu or turn menu.

---

## Build Metropolis Menu

Shown when player selects `[5] Metropolis` from Build Menu.

**Display (A-Side):**
```
=== Build Metropolis (A) ===
Available metropolises (2 remaining):

[1] METROPOLIS A
    Cost: 3 wool, 1 ore
    +2 resources per Harvest phase
    Worth: 3 VPs

[2] METROPOLIS A
    Cost: 3 wool, 1 ore
    +2 resources per Harvest phase
    Worth: 3 VPs

[B] Back   [Q] Quit
```

**Display (B-Side):**
```
=== Build Metropolis (B) ===
Available metropolises (2 remaining):

[1] ROAD METROPOLIS
    Cost: 3 wool, 1 ore
    +1 resource per Harvest phase
    On build: draw 1 resource per road you have built
    Worth: 3 VPs

[2] LARGEST-ARMY METROPOLIS
    Cost: 3 wool, 1 ore
    +1 resource per Harvest phase
    Wins all ties for Largest Army
    Worth: 3 VPs

[B] Back   [Q] Quit
```

**Options:**
- `[1]` through `[N]` (where N = remaining metropolises) → Build the selected metropolis, execute B-side upon-build effects if any, return to Build menu.
- `[B]` → Back to Build menu
- `[Q]` → Final Scoreboard, exit

**If no metropolises remain:**
```
All metropolises have been built.

[B] Back   [Q] Quit
```

---

## Trade Flow (Multi-Step)

### Trade Step 1: Offer Selection

**Prompt:**
```
=== Trade — Step 1 of 4 ===
Your hand:
  1. brick  x2
  2. wood   x1
  3. wool   x0
  4. wheat  x3
  5. ore    x1

Which resource type(s) are you willing to offer? (comma-separated)
> _
```

**Input:** Comma-separated numbers (e.g., `1,4` for brick and wheat).

**Validation:**
- At least one number selected
- All selected types must have count > 0
- Numbers must be in range 1-5

**Navigation:**
- Valid input → Trade Step 2
- Invalid input → Red error message, re-prompt
- `[B]` → Back to Turn Menu
- `[Q]` → Final Scoreboard, exit

---

### Trade Step 2: Request Selection

**Prompt:**
```
=== Trade — Step 2 of 4 ===
You are offering: brick OR wheat

What do you want in return? (single number)
  1. wood
  2. wool
  3. ore
[B] Back   [Q] Quit
> _
```

**Input:** Single number (1-5).

**Validation:**
- Single number (not comma-separated)
- Number in range 1-5
- Selected type must differ from all offered types

**Navigation:**
- Valid input → Trade Step 3
- Invalid input → Red error, re-prompt
- `[B]` → Back to Trade Step 1
- `[Q]` → Final Scoreboard, exit

---

### Trade Step 3: Partner Selection

**Prompt:**
```
=== Trade — Step 3 of 4 ===
You offer: brick OR wheat
You want: ore

Trade with whom?
[1] Bob
[2] Carol
[B] Back   [Q] Quit
```

**Options:**
- `[1]` or `[2]` → Trade Step 4
- `[B]` → Back to Trade Step 2
- `[Q]` → Final Scoreboard, exit

---

### Trade Step 4: Confirm Proposal

**Prompt:**
```
=== Trade — Step 4 of 4 ===
Summary: offer brick OR wheat to Bob in exchange for 1 ore.

[1] Propose trade to Bob
[B] Back (change offer)
[Q] Quit
```

**Options:**
- `[1]` → Proceed to partner decision. Auto-decline check: if Bob has 0 ore, trade is auto-declined and action marked as used. Otherwise, show partner decision prompt.
- `[B]` → Back to Trade Step 1
- `[Q]` → Final Scoreboard, exit

---

### Partner Decision Prompt

Screen clears. Pass-device flow:

```
════════════════════════════════════════════════════════════
Pass the device to BOB.

Press ENTER when BOB is ready...
════════════════════════════════════════════════════════════
```

Then:

```
=== BOB, ALICE is offering you a trade ===
Your hand:
  1. brick  x1
  2. wood   x0
  3. wool   x2
  4. wheat  x0
  5. ore    x1

Alice is offering:
  1. brick
  2. wheat
Alice wants: 1 ore

Accept? [Y/N]
> _
```

**Input:** `Y` or `N` (case-insensitive).

**Navigation:**
- `Y` → Partner Type Selection (if multiple offered types) or Transfer Execution
- `N` → Decline message, pass-device back to current player, return to Turn Menu
- Trade action is marked as used regardless of accept/decline

---

### Partner Type Selection (If Multiple Offered)

If Alice offered 2+ types:

```
Pick which to give Alice:
[1] brick
[2] wheat
[B] Back   [Q] Quit
> _
```

**Options:**
- `[1]` or `[2]` → Execute trade, current player draws bonus resource
- `[B]` → Back to accept/decline prompt (re-ask)
- `[Q]` → Final Scoreboard, exit

---

### Trade Outcome (Accept)

Screen clears. Pass-device:

```
════════════════════════════════════════════════════════════
Pass the device to ALICE.

Press ENTER when ALICE is ready...
════════════════════════════════════════════════════════════
```

Then:

```
=== Trade accepted ===
You gave Bob: 1 brick
You received from Bob: 1 ore
Bob drew a bonus resource card.

[Press ENTER to return to your turn menu]
```

**Navigation:**
- ENTER → Back to Turn Menu

---

### Trade Outcome (Decline or Auto-Decline)

For decline:

```
=== Trade declined ===
Bob declined the trade.

[Press ENTER to return to your turn menu]
```

For auto-decline (target has 0 of requested type):

```
=== Trade auto-declined ===
Bob has no ore to trade.

[Press ENTER to return to your turn menu]
```

**Navigation:**
- ENTER → Back to Turn Menu

---

## Substitute Menu

**Prompt:**
```
=== Substitute resources ===
Your roads: 3 (substitution ratio 2:1)
Your hand:
  1. brick  x2
  2. wood   x1
  3. wool   x4
  4. wheat  x0
  5. ore    x1

Which resource to give up? (need at least 2 of the chosen type)
> _
```

**Input:** Single number (1-5).

**Validation:**
- Number in range 1-5
- Selected type must have count >= ratio denominator

**Navigation:**
- Valid input → Substitute Target Selection
- Invalid input → Red error, re-prompt
- `[B]` → Back to Turn Menu
- `[Q]` → Final Scoreboard, exit

---

### Substitute Target Selection

**Prompt:**
```
You will give up 2 wool. Which resource do you want?
[1] brick
[2] wood
[3] wheat     <- (the selected type is not shown; only others)
[4] ore
[B] Back   [Q] Quit
> _
```

**Options:**
- Any enabled option → Substitute Confirmation
- `[B]` → Back to Substitute (source type re-selection)
- `[Q]` → Final Scoreboard, exit

---

### Substitute Confirmation

**Prompt:**
```
Confirm: give 2 wool, receive 1 brick? [Y/N]
> _
```

**Input:** `Y` or `N`.

**Navigation:**
- `Y` → Execute substitution, mark substitute as used, return to Turn Menu
- `N` → Back to Substitute Target Selection

---

## End Turn Confirmation

**Prompt:**
```
End your turn? [Y/N]
> _
```

**Input:** `Y` or `N`.

**Navigation:**
- `Y` → End turn. If current player has 7+ VPs, show Final Scoreboard (WINNER). Otherwise, pass-device prompt, next player's harvest phase, next player's Turn Menu.
- `N` → Back to Turn Menu

---

## Pass-Device Prompt

Used between turns and within multi-step flows (trade, charity event):

```
════════════════════════════════════════════════════════════
Pass the device to BOB.

Press ENTER when BOB is ready...
════════════════════════════════════════════════════════════
```

Screen clears after ENTER.

---

## Event Card Resolution Displays

### Robber Event

```
=== Event card drawn: ROBBER ===
Each player with more than (7 + their knight count) resource cards
must discard half their hand, rounded down.

Alice: 12 cards, 1 knight. Threshold: 8. Must discard 6 cards.
Bob:    5 cards, 0 knights. Threshold: 7. No discard.
Carol:  9 cards, 0 knights. Threshold: 7. Must discard 4 cards.

Press ENTER to continue...
```

For each player who must discard, pass-device prompt, then:

```
=== ALICE, discard 6 cards ===
Your hand:
  1. brick  x2
  2. wood   x1
  3. wool   x4
  4. wheat  x0
  5. ore    x1

Discard (remaining: 6): enter card number
> _
```

Repeat until discard count is reached. Then pass-device to next player or back to the builder.

---

### Abundance Event

```
=== Event card drawn: ABUNDANCE ===
Players without Longest Route or Largest Army each draw 2 resources.

Alice: drew [wood, wheat]
Bob: already has Longest Route, no draw
Carol: drew [ore, brick]

[Press ENTER to return to your turn menu]
```

---

### Charity Event

Depends on charity case. Example (Case A: one top, one bottom):

```
=== Event card drawn: CHARITY ===
Players with the most VPs give 1 resource to players with the fewest VPs.

Alice (5 VP, most): which resource to give?
[1] brick  x1
[2] wool   x3
[3] ore    x1
> _
```

---

### Solstice Event

```
=== Event card drawn: SOLSTICE ===
Each player draws 1 resource. All event cards are reshuffled.

Alice: drew [brick]
Bob: drew [wheat]
Carol: drew [ore]

Event deck reshuffled. 7 cards now in deck.

[Press ENTER to return to your turn menu]
```

---

### Subsidy Event

```
=== Event card drawn: SUBSIDY ===
Each player draws 1 resource per uncovered settlement.

Alice (1 settlement): drew [wood]
Bob (2 settlements): drew [brick, ore]
Carol (0 settlements): no draw

[Press ENTER to return to your turn menu]
```

---

## Final Scoreboard

Shown on game end (win or quit).

**Display:**
```
╔══════════════════════ FINAL SCORES ══════════════════════╗
║                                                          ║
║  ALICE ............................... 7 VPs  WINNER    ║
║    Settlements: 2  Cities: 1  Metropolises: 1            ║
║    Roads: 4  Knights: 1                                  ║
║    Longest Route: YES  Largest Army: NO                  ║
║    Final hand: 3 brick, 2 wool, 1 ore                    ║
║                                                          ║
║  BOB .................................. 5 VPs           ║
║    Settlements: 3  Cities: 1  Metropolises: 0            ║
║    Roads: 3  Knights: 2                                  ║
║    Longest Route: NO  Largest Army: YES                  ║
║    Final hand: 1 wood, 4 wheat                           ║
║                                                          ║
║  CAROL ................................ 3 VPs           ║
║    Settlements: 3  Cities: 0  Metropolises: 0            ║
║    Roads: 2  Knights: 0                                  ║
║    Longest Route: NO  Largest Army: NO                   ║
║    Final hand: 2 brick, 1 wood, 1 ore                    ║
║                                                          ║
║  Rounds played: 12                                       ║
║  Seed: 42                                                ║
╚══════════════════════════════════════════════════════════╝
```

On win, winner's line is bold and green. On quit without winner, no WINNER tag.

Application exits after scoreboard is displayed.

---

## Menu Navigation Summary

```
Top Menu
  ├─ [1] New Game
  │   └─ Setup Flow (5 steps)
  │       └─ Summary
  │           └─ Turn Menu (repeat)
  │               ├─ [1] View Board
  │               ├─ [2] View Hand
  │               ├─ [3] Trade (multi-step)
  │               ├─ [4] Build
  │               │   └─ [5] Metropolis (if selected)
  │               ├─ [5] Substitute
  │               ├─ [6] End Turn (confirm)
  │               └─ [Q] Quit
  │                   └─ Final Scoreboard
  └─ [Q] Quit → Exit
```

Every menu (except Top) has `[B] Back` and `[Q] Quit` options unless otherwise noted.
