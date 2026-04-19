package com.enrique.catanontheroad.shell.display;

import com.enrique.catanontheroad.game.Game;
import com.enrique.catanontheroad.game.Player;
import com.enrique.catanontheroad.game.bonus.LargestArmy;
import com.enrique.catanontheroad.game.bonus.LongestRoute;

public class PlayerAreaRenderer {

    public String renderTurnHeader(Game game) {
        Player player = game.getCurrentPlayer();
        LongestRoute lr = game.getLongestRoute();
        LargestArmy la = game.getLargestArmy();

        StringBuilder sb = new StringBuilder();
        sb.append("════════════════════════════════════════════════════════════\n");
        sb.append("=== ").append(AnsiColors.bold(player.getName())).append("'s turn — Round ")
          .append(game.getRound()).append(" ===\n");
        sb.append("VPs: ").append(player.calculateVictoryPoints())
          .append("    Hand size: ").append(player.getHand().total()).append("\n");
        sb.append("Roads: ").append(player.getRoadCount())
          .append("    Settlements: ").append(player.getUncoveredSettlementCount())
          .append("    Cities: ").append(player.getUncoveredCityCount())
          .append("    Knights: ").append(player.getKnightCount())
          .append("    Metropolises: ").append(player.getMetropolisCount()).append("\n");

        sb.append("Longest Route: ");
        if (lr.hasHolder()) {
            sb.append(lr.getHolder().getName())
              .append(" (").append(lr.getHolderRoadCount()).append(" roads)");
        } else {
            sb.append("— (no holder)");
        }

        sb.append("    Largest Army: ");
        if (la.hasHolder()) {
            sb.append(la.getHolder().getName())
              .append(" (").append(la.getHolderKnightCount()).append(" knights)");
        } else {
            sb.append("— (no holder)");
        }
        sb.append("\n");

        sb.append("════════════════════════════════════════════════════════════\n");
        return sb.toString();
    }
}
