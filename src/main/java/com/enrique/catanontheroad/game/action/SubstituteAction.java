package com.enrique.catanontheroad.game.action;

import com.enrique.catanontheroad.game.Game;
import com.enrique.catanontheroad.game.Player;
import com.enrique.catanontheroad.game.card.ResourceType;

public class SubstituteAction {

    public record SubstituteResult(boolean success, String message) {
        public static SubstituteResult success(String message) {
            return new SubstituteResult(true, message);
        }

        public static SubstituteResult failure(String message) {
            return new SubstituteResult(false, message);
        }
    }

    public record ValidationResult(boolean isValid, String message) {
        public static ValidationResult valid() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult invalid(String message) {
            return new ValidationResult(false, message);
        }
    }

    public int getRatio(Player player) {
        return player.getSubstitutionRatio();
    }

    public ValidationResult canSubstitute(Player player, ResourceType sourceType, ResourceType targetType) {
        if (sourceType == targetType) {
            return ValidationResult.invalid("Source and target types must be different");
        }

        int ratio = getRatio(player);
        int available = player.getHand().count(sourceType);

        if (available < ratio) {
            return ValidationResult.invalid(
                "Need at least " + ratio + " " + sourceType.name().toLowerCase() +
                " cards, but only have " + available
            );
        }

        return ValidationResult.valid();
    }

    public SubstituteResult execute(Player player, ResourceType sourceType, ResourceType targetType, Game game) {
        ValidationResult validation = canSubstitute(player, sourceType, targetType);
        if (!validation.isValid()) {
            return SubstituteResult.failure(validation.message());
        }

        int ratio = getRatio(player);

        // Remove source cards and discard them
        for (int i = 0; i < ratio; i++) {
            player.getHand().remove(sourceType);
            game.getResourceDeck().discard(sourceType);
        }

        // Draw target card (from deck, not discard)
        // Actually per spec, gain 1 card of target type - draw from deck
        ResourceType drawn = game.getResourceDeck().draw();
        // But we want a specific type... The spec says "gains 1 card of the target type"
        // This implies we don't draw randomly, we get the specific type.
        // Let's put back the drawn card and instead just add the target type directly
        game.getResourceDeck().discard(drawn);
        player.getHand().add(targetType);

        return SubstituteResult.success(
            "Substituted " + ratio + " " + sourceType.name().toLowerCase() +
            " for 1 " + targetType.name().toLowerCase()
        );
    }
}
