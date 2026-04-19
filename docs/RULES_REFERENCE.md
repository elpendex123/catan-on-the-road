# Rules Reference

Complete game rules as implemented.

## Players

Fixed at 3 players. Turn order follows name entry order.

## Resources

Five types: **brick**, **wood**, **wool**, **wheat**, **ore**

Resource deck (61 cards):
- 11 brick, 11 wood, 14 wool, 12 wheat, 13 ore

## Building Cards

Building deck (34 cards): 10 roads, 11 settlements, 5 cities, 8 knights

| Building | Cost | VP | Notes |
|----------|------|----|----|
| Road | 1 brick + 1 wood | 0 | Improves substitution ratio |
| Settlement | 1 brick + 1 wood + 1 wool + 1 wheat | 1 | Triggers event card |
| City | 2 wheat + 3 ore | 2 | Requires uncovered settlement |
| Knight | 1 wool + 1 ore | 0 | Raises robber threshold |
| Metropolis | 3 wool + 1 ore | 3 | Requires uncovered city |

## Metropolises

4 metropolises per game, all same side (A or B).

### Side A
- +2 harvest draw per uncovered metropolis
- No special abilities
- All 4 are identical

### Side B
- +1 harvest draw per uncovered metropolis
- Each has unique ability:

| Type | Ability |
|------|---------|
| Road | On build: draw 1 resource per road owned |
| Longest-Route | Wins all ties for Longest Route |
| Knight | On build: draw 1 resource per knight owned |
| Largest-Army | Wins all ties for Largest Army |

## Turn Structure

### 1. Harvest Phase (automatic)
- All players draw 1 base resource card
- Current player draws bonus cards:
  - +1 per uncovered city
  - +2 per uncovered Metropolis A (or +1 for B)

### 2. Action Phase
Available actions:

| Action | Limit | Description |
|--------|-------|-------------|
| Build | Unlimited | Build roads, settlements, cities, knights, metropolises |
| Trade | Once/turn | 1:1 swap with another player |
| Substitute | Once/turn | Convert resources at road-based ratio |
| View | Unlimited | Check board state and hand |
| End turn | - | Pass to next player |

## Building Rules

### Building Row
- Always 5 cards visible
- Initial row guaranteed: 1 road, 1 settlement, 1 city, 1 knight, +1 random
- If all 5 same type: reshuffle into deck and redeal

### Placement
- **Cities** placed on uncovered settlements (settlement becomes covered)
- **Metropolises** placed on uncovered cities (city becomes covered)
- Covered buildings don't count for VP or effects

## Trade Rules

1. Offerer selects resource type(s) to offer
2. Offerer selects type wanted in return
3. Offerer selects trade partner
4. If partner lacks requested type: auto-decline
5. Partner accepts or declines
6. On accept: 1:1 swap, partner draws bonus card
7. Trade action used regardless of outcome

## Substitution

Convert N cards of one type to 1 card of another type.

| Roads Owned | Ratio |
|-------------|-------|
| 1 | 4:1 |
| 2 | 3:1 |
| 3 | 2:1 |
| 4+ | 1:1 |

## Event Cards

Drawn when building a settlement. Deck (7 cards): 3 Robber, 1 each other.

### Robber
Each player with more than `7 + knight_count` cards must discard half (rounded down).

### Abundance
Each player **except** Longest Route and Largest Army holders draws 2 cards.

### Charity
Players with most VP give 1 resource each to players with fewest VP.
- If all tied: nothing happens
- Givers with empty hand skip giving

### Solstice
Each player draws 1 card. Then reshuffle all 7 event cards.

### Subsidy
Each player draws 1 card per uncovered settlement.

## Bonus Cards

### Longest Route (2 VP)
- First to 3+ roads claims it
- Transfers on strictly more roads
- A-side: holder keeps on ties
- B-side: B-Longest-Route metropolis holder wins ties

### Largest Army (2 VP)
- First to 2+ knights claims it
- Transfers on strictly more knights
- A-side: holder keeps on ties
- B-side: B-Largest-Army metropolis holder wins ties

## Victory Points

| Source | VP |
|--------|---:|
| Uncovered Settlement | 1 |
| Uncovered City | 2 |
| Uncovered Metropolis | 3 |
| Longest Route | 2 |
| Largest Army | 2 |

**Win condition:** 7+ VP on your own turn.

## Deck Exhaustion

| Deck | On Empty |
|------|----------|
| Resource | Shuffle discard pile |
| Event | Auto-reshuffle (or Solstice triggers it) |
| Building | Shuffle discard pile |
| Metropolis | No reshuffle (only 4 exist) |
