# Current Issues — In Progress

Last updated: 2026-04-20

## Issue #1: Seed Input Blank Not Accepted

**Status:** Partially fixed (code changes made, needs testing)

**Description:**  
When entering a seed at setup, pressing ENTER with a blank input shows "Input cannot be empty." instead of accepting it as a request for a random seed.

**Root Cause:**  
`MenuPrompt.promptText()` default behavior rejects empty input. Seed input needs to allow blank.

**Reproduction:**
1. Start new game
2. Enter player names
3. Select metropolis side
4. At "Enter seed" prompt, press ENTER without typing anything
5. Error: "Input cannot be empty."

**Expected Behavior:**  
- Allow blank input to mean "use random seed"
- Display guidance text showing seed is optional and what to expect (e.g., "blank for random, or any number")

**Changes Made:**
- Added overloaded `promptText(String prompt, boolean showBack, boolean showQuit, boolean allowBlank)` method to `MenuPrompt.java`
- Updated `SetupMenu.java` to call new overload with `allowBlank=true` for seed input
- Updated prompt text to "Enter seed (blank for random, or any number):"

**Testing Required:**
- Verify blank seed input is accepted
- Verify seed is set to `System.currentTimeMillis()` when blank
- Verify numeric seed input still works
- Verify 100% test coverage maintained

---

## Issue #2: Turn Header Display Needs Color and Centering

**Status:** Not started

**Description:**  
The turn header line is displayed plainly without color or special formatting. It should be:
- Centered
- Colored (cyan border, bold title text)
- Longer border line (70+ characters)

**Current Display:**
```
════════════════════════════════════════════════════════════
=== enrique's turn — Round 1 ===
VPs: 1    Hand size: 3
Roads: 1    Settlements: 1    Cities: 0    Knights: 0    Metropolises: 0
Longest Route: — (no holder)    Largest Army: — (no holder)
════════════════════════════════════════════════════════════
```

**Expected Display:**
```
═════════════════════════════════════════════════════════════════════
=== ALICE's turn — Round 4 ===
═════════════════════════════════════════════════════════════════════
VPs: 3    Hand size: 5
Roads: 2    Settlements: 1    Cities: 1    Knights: 0    Metropolises: 0
Longest Route: BOB (3 roads)    Largest Army: — (no holder)
```

**Required Changes:**
- Top border line: `AnsiColors.cyan()` with 73 character width (═ characters)
- Title line: `AnsiColors.bold()` with proper spacing/centering
- Bottom border line: same as top
- Possibly in `TurnMenu.java` or where turn header is rendered

**Files to Modify:**
- `TurnMenu.java` (likely location of turn menu rendering)

---

## Issue #3: Building Row Display Alignment and Spacing

**Status:** Partially fixed (needs refinement)

**Description:**  
The building row rendering has incorrect alignment and spacing. Cards wrap incorrectly and costs are not properly aligned under card names.

**Current Display:**
```
╔════════════════════════════ BUILDING ROW ════════════════════════════════╗
║                                                                          ║
║  [1] KNIGHT         [2] SETTLEMENT     [3] ROAD           [4] SETTLEMENT     [5] CITY
║  1 ore             1 wheat             1 brick             1 wheat             3 ore           ║
║  1 wool             1 brick             1 wood             1 brick             2 wheat           ║
║             1 wool                        1 wool                      ║
║             1 wood                        1 wood                      ║
```

**Expected Display:**
```
╔═══════════════════════════ BUILDING ROW ═══════════════════════════════════════════════════╗
║                                                                                            ║
║  [1] KNIGHT          [2] SETTLEMENT      [3] ROAD         [4] SETTLEMENT      [5] CITY     ║
║      1 ore               1 brick             1 wood           1 brick             2 wheat  ║
║      1 wool              1 wood              1 brick          1 wood              3 ore    ║
║                          1 wool                               1 wool                       ║
║                          1 wheat                              1 wheat                      ║
║                                                                                            ║
╚════════════════════════════════════════════════════════════════════════════════════════════╝
```

**Issues:**
1. Cost items need to be properly aligned under their respective card columns
2. Border is too short; needs 97-98 characters
3. Spacing/padding between columns needs refinement
4. Card name labels need consistent spacing

**Files to Modify:**
- `BoardRenderer.java` (method: `renderBuildingRow()`)

**Changes to Make:**
- Increase border width to ~97 characters
- Adjust column width calculations
- Use consistent spacing/padding for alignment
- Ensure each cost item aligns under the correct card

---

## Issue #4: Metropolis Side Restriction Not Enforced

**Status:** Not started (Critical bug)

**Description:**  
When a player selects A-side metropolises at game setup, the build metropolis menu still shows B-side metropolises with their unique abilities (ROAD, LONGEST ROUTE, KNIGHT, LARGEST ARMY). The display title correctly says "(A)" but the content is wrong.

**Current Behavior (when A-side selected):**
```
=== Build Metropolis (A) ===
Available metropolises (4 remaining):

[1] ROAD METROPOLIS
    Cost: 3 wool, 1 ore
    +2 resources per Harvest phase
    Worth: 3 VPs

[2] LONGEST ROUTE METROPOLIS
    Cost: 3 wool, 1 ore
    +2 resources per Harvest phase
    Worth: 3 VPs

[3] KNIGHT METROPOLIS
    Cost: 3 wool, 1 ore
    +2 resources per Harvest phase
    Worth: 3 VPs

[4] LARGEST ARMY METROPOLIS
    Cost: 3 wool, 1 ore
    +2 resources per Harvest phase
    Worth: 3 VPs
```

**Expected Behavior (A-side selected):**
All 4 metropolises should display identically:
```
=== Build Metropolis (A) ===
Available metropolises (4 remaining):

[1] METROPOLIS
    Cost: 3 wool, 1 ore
    +2 resources per Harvest phase
    Worth: 3 VPs

[2] METROPOLIS
    Cost: 3 wool, 1 ore
    +2 resources per Harvest phase
    Worth: 3 VPs

[3] METROPOLIS
    Cost: 3 wool, 1 ore
    +2 resources per Harvest phase
    Worth: 3 VPs

[4] METROPOLIS
    Cost: 3 wool, 1 ore
    +2 resources per Harvest phase
    Worth: 3 VPs
```

**Root Cause:**  
The metropolis building menu is likely displaying cards from the metropolis stack without checking the game's `MetropolisSide` setting. It may be using the default or wrong side.

**Files to Check/Modify:**
- `BuildMenu.java` (method likely related to metropolis building)
- Possibly `Game.java` or `GameService.java` for metropolis side access
- `MetropolisCard.java` or similar for card generation/display

**Required Fix:**
- Verify `MetropolisSide` is stored in `Game` or accessible from game service
- When building metropolis, retrieve only cards matching the selected side
- Update `BuildMenu` to enforce this restriction
- Update display logic to show appropriate descriptions based on side

---

## Priority Order

1. **Issue #4** (Critical) — Wrong metropolis side displayed
2. **Issue #1** (High) — Seed input won't accept blank (partially fixed)
3. **Issue #2** (Medium) — Turn header formatting
4. **Issue #3** (Medium) — Building row alignment

## Testing Strategy

After each fix:
1. Verify the specific issue is resolved through manual gameplay
2. Run `./gradlew test` to ensure no regressions
3. Check `./gradlew jacocoTestCoverageVerification` to maintain 100% coverage
4. Update test files if new code paths are added

