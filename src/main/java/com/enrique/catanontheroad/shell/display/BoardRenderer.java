package com.enrique.catanontheroad.shell.display;

import com.enrique.catanontheroad.game.Game;
import com.enrique.catanontheroad.game.Player;
import com.enrique.catanontheroad.game.card.BuildingCard;
import com.enrique.catanontheroad.game.card.ResourceType;

import java.util.List;
import java.util.Map;

public class BoardRenderer {

    public String renderBuildingRow(Game game) {
        List<BuildingCard> row = game.getBuildingDeck().getBuildingRow();
        StringBuilder sb = new StringBuilder();

        sb.append("╔═══════════════════════════ BUILDING ROW ═══════════════════════════╗\n");
        sb.append("║                                                                    ║\n");

        // Render cards in rows of up to 3
        for (int i = 0; i < row.size(); i++) {
            if (i % 3 == 0 && i > 0) {
                sb.append("║                                                                    ║\n");
            }
            BuildingCard card = row.get(i);
            if (i % 3 == 0) sb.append("║  ");
            sb.append("[").append(i + 1).append("] ").append(formatCardName(card));
            if (i % 3 == 2 || i == row.size() - 1) {
                sb.append("\n");
                renderCardCost(sb, row, i - (i % 3), Math.min(i + 1, row.size()));
            }
        }

        sb.append("║                                                                    ║\n");
        sb.append("╚════════════════════════════════════════════════════════════════════╝\n");

        return sb.toString();
    }

    private void renderCardCost(StringBuilder sb, List<BuildingCard> row, int from, int to) {
        int maxCostLines = 0;
        for (int i = from; i < to; i++) {
            maxCostLines = Math.max(maxCostLines, row.get(i).cost().size());
        }

        for (int line = 0; line < maxCostLines; line++) {
            sb.append("║  ");
            for (int i = from; i < to; i++) {
                Map<ResourceType, Integer> cost = row.get(i).cost();
                ResourceType[] types = cost.keySet().toArray(new ResourceType[0]);
                if (line < types.length) {
                    ResourceType type = types[line];
                    sb.append("  ").append(cost.get(type)).append(" ")
                      .append(AnsiColors.colorResource(type));
                }
                sb.append("              ");
            }
            sb.append("\n");
        }
    }

    private String formatCardName(BuildingCard card) {
        return String.format("%-16s", card.type().name());
    }

    public String renderPlayerAreas(Game game) {
        List<Player> players = game.getPlayers();
        StringBuilder sb = new StringBuilder();

        for (Player player : players) {
            String longestMark = player.hasLongestRoute() ? " *" : "";
            String armyMark = player.hasLargestArmy() ? " +" : "";

            sb.append("╔══ ").append(player.getName())
              .append(" (").append(player.calculateVictoryPoints()).append(" VP) ══╗\n");
            sb.append("║ Roads:        ").append(String.format("%-3d", player.getRoadCount())).append(longestMark).append("\n");
            sb.append("║ Settlements:  ").append(String.format("%-3d", player.getUncoveredSettlementCount())).append("\n");
            sb.append("║ Cities:       ").append(String.format("%-3d", player.getUncoveredCityCount())).append("\n");
            sb.append("║ Knights:      ").append(String.format("%-3d", player.getKnightCount())).append(armyMark).append("\n");
            sb.append("║ Metropolises: ").append(String.format("%-3d", player.getMetropolisCount())).append("\n");
            sb.append("║ Hand size:    ").append(String.format("%-3d", player.getHand().total())).append("\n");
            sb.append("╚══════════════════╝\n\n");
        }

        sb.append("* = Longest Route    + = Largest Army\n");
        return sb.toString();
    }
}
