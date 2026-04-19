package com.enrique.catanontheroad.shell.display;

import com.enrique.catanontheroad.game.Game;
import com.enrique.catanontheroad.game.Player;

import java.util.Comparator;
import java.util.List;

public class ScoreboardRenderer {

    private final HandRenderer handRenderer;

    public ScoreboardRenderer() {
        this.handRenderer = new HandRenderer();
    }

    public String render(Game game, long seed) {
        List<Player> players = game.getPlayers().stream()
            .sorted(Comparator.comparingInt(Player::calculateVictoryPoints).reversed())
            .toList();

        Player winner = game.getWinner();

        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════ FINAL SCORES ══════════════════════╗\n");
        sb.append("║                                                          ║\n");

        for (Player player : players) {
            int vp = player.calculateVictoryPoints();
            boolean isWinner = winner != null && player == winner;

            String nameLine = "  " + player.getName() + " ";
            String dots = ".".repeat(Math.max(1, 40 - nameLine.length()));
            String vpStr = " " + vp + " VPs";
            String winTag = isWinner ? "  WINNER" : "";

            if (isWinner) {
                sb.append("║").append(AnsiColors.boldGreen(
                    nameLine + dots + vpStr + winTag
                )).append("\n");
            } else {
                sb.append("║").append(nameLine).append(dots).append(vpStr).append(winTag).append("\n");
            }

            sb.append("║    Settlements: ").append(player.getUncoveredSettlementCount())
              .append("  Cities: ").append(player.getUncoveredCityCount())
              .append("  Metropolises: ").append(player.getMetropolisCount()).append("\n");
            sb.append("║    Roads: ").append(player.getRoadCount())
              .append("  Knights: ").append(player.getKnightCount()).append("\n");
            sb.append("║    Longest Route: ").append(player.hasLongestRoute() ? "YES" : "NO")
              .append("  Largest Army: ").append(player.hasLargestArmy() ? "YES" : "NO").append("\n");
            sb.append("║    Final hand: ").append(handRenderer.renderFinalHand(player)).append("\n");
            sb.append("║                                                          ║\n");
        }

        sb.append("║  Rounds played: ").append(game.getRound()).append("\n");
        sb.append("║  Seed: ").append(seed).append("\n");
        sb.append("╚══════════════════════════════════════════════════════════╝\n");

        return sb.toString();
    }
}
