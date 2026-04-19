package com.enrique.catanontheroad.game.bonus;

import com.enrique.catanontheroad.game.Player;
import com.enrique.catanontheroad.game.card.Metropolis;
import com.enrique.catanontheroad.game.card.MetropolisSide;
import com.enrique.catanontheroad.game.card.MetropolisType;

import java.util.List;

public class LargestArmy {

    private static final int MINIMUM_KNIGHTS = 2;

    private Player holder;

    public LargestArmy() {
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
                if (player.getKnightCount() >= MINIMUM_KNIGHTS) {
                    setHolder(player);
                    return;
                }
            }
        } else {
            // Check if someone else should take it
            Player currentHolder = holder;
            int currentKnights = currentHolder.getKnightCount();

            Player tieBreakHolder = findTieBreakHolder(players, metropolisSide);

            for (Player player : players) {
                if (player == currentHolder) continue;

                int playerKnights = player.getKnightCount();

                if (playerKnights > currentKnights) {
                    // Strictly more knights - takes the bonus
                    setHolder(player);
                    return;
                } else if (playerKnights == currentKnights && tieBreakHolder != null) {
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
                if (m.hasTieBreakForLargestArmy()) {
                    return player;
                }
            }
        }
        return null;
    }

    private void setHolder(Player newHolder) {
        if (holder != null) {
            holder.setLargestArmy(false);
        }
        holder = newHolder;
        if (holder != null) {
            holder.setLargestArmy(true);
        }
    }

    public int getHolderKnightCount() {
        return holder != null ? holder.getKnightCount() : 0;
    }
}
