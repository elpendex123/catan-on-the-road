package com.enrique.catanontheroad.shell.display;

import com.enrique.catanontheroad.game.Hand;
import com.enrique.catanontheroad.game.Player;
import com.enrique.catanontheroad.game.card.ResourceType;

public class HandRenderer {

    public String render(Player player) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ").append(player.getName()).append("'s hand ===\n");
        Hand hand = player.getHand();

        int index = 1;
        for (ResourceType type : ResourceType.values()) {
            String name = type.name().toLowerCase();
            sb.append("  ").append(index).append(". ")
              .append(AnsiColors.colorResource(name))
              .append("  x").append(hand.count(type)).append("\n");
            index++;
        }
        sb.append("\nTotal: ").append(hand.total()).append(" cards\n");
        return sb.toString();
    }

    public String renderFinalHand(Player player) {
        Hand hand = player.getHand();
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (ResourceType type : ResourceType.values()) {
            int count = hand.count(type);
            if (count > 0) {
                if (!first) sb.append(", ");
                sb.append(count).append(" ").append(AnsiColors.colorResource(type));
                first = false;
            }
        }
        if (first) {
            sb.append("(empty)");
        }
        return sb.toString();
    }
}
