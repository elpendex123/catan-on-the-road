package com.enrique.catanontheroad.game.bonus;

import com.enrique.catanontheroad.game.Player;
import com.enrique.catanontheroad.game.card.Metropolis;
import com.enrique.catanontheroad.game.card.MetropolisSide;
import com.enrique.catanontheroad.game.card.MetropolisType;

import java.util.List;

public class LongestRoute {

    private static final int MINIMUM_ROADS = 3;

    private Player holder;

    public LongestRoute() {
        this.holder = null;
    }

    public Player getHolder() {
        return holder;
    }

    public boolean hasHolder() {
        return holder != null;
    }

    public void checkAndUpdate(List<Player> players, MetropolisSide metropolisSide) {
        if (!hasHolder()) {
            // First to reach minimum wins
            for (Player player : players) {
                if (player.getRoadCount() >= MINIMUM_ROADS) {
                    setHolder(player);
                    return;
                }
            }
        } else {
            // Check if someone else should take it
            Player currentHolder = holder;
            int currentRoads = currentHolder.getRoadCount();

            Player tieBreakHolder = findTieBreakHolder(players, metropolisSide);

            for (Player player : players) {
                if (player == currentHolder) continue;

                int playerRoads = player.getRoadCount();

                if (playerRoads > currentRoads) {
                    // Strictly more roads - takes the bonus
                    setHolder(player);
                    return;
                } else if (playerRoads == currentRoads && tieBreakHolder != null) {
                    // Tie - check if tie-break holder should win
                    if (player == tieBreakHolder && currentHolder != tieBreakHolder) {
                        setHolder(tieBreakHolder);
                        return;
                    }
                }
            }
        }
    }

    private Player findTieBreakHolder(List<Player> players, MetropolisSide metropolisSide) {
        if (metropolisSide != MetropolisSide.B) {
            return null; // A-side has no tie-break
        }

        for (Player player : players) {
            for (Metropolis m : player.getMetropolises()) {
                if (m.hasTieBreakForLongestRoute()) {
                    return player;
                }
            }
        }
        return null;
    }

    private void setHolder(Player newHolder) {
        if (holder != null) {
            holder.setLongestRoute(false);
        }
        holder = newHolder;
        if (holder != null) {
            holder.setLongestRoute(true);
        }
    }

    public int getHolderRoadCount() {
        return holder != null ? holder.getRoadCount() : 0;
    }
}
