package com.enrique.catanontheroad.game.action;

import com.enrique.catanontheroad.game.Game;
import com.enrique.catanontheroad.game.Player;
import com.enrique.catanontheroad.game.card.ResourceType;

import java.util.List;

public class TradeAction {

    public record TradeOffer(
        Player offerer,
        List<ResourceType> offeredTypes,
        ResourceType requestedType,
        Player target
    ) {}

    public record TradeResult(boolean success, String message, ResourceType givenType, ResourceType receivedType) {
        public static TradeResult declined(String message) {
            return new TradeResult(false, message, null, null);
        }

        public static TradeResult autoDeclined(String message) {
            return new TradeResult(false, message, null, null);
        }

        public static TradeResult accepted(ResourceType given, ResourceType received) {
            return new TradeResult(true, "Trade accepted", given, received);
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

    public ValidationResult validateOffer(Player offerer, List<ResourceType> offeredTypes, ResourceType requestedType) {
        if (offeredTypes == null || offeredTypes.isEmpty()) {
            return ValidationResult.invalid("Must offer at least one resource type");
        }

        // Check offerer has at least one of each offered type
        for (ResourceType type : offeredTypes) {
            if (!offerer.getHand().has(type)) {
                return ValidationResult.invalid("You don't have any " + type.name().toLowerCase() + " to offer");
            }
        }

        // Check requested type is different from all offered types
        if (offeredTypes.contains(requestedType)) {
            return ValidationResult.invalid("Requested type cannot be in the offered types");
        }

        return ValidationResult.valid();
    }

    public boolean canAutoDecline(Player target, ResourceType requestedType) {
        return !target.getHand().has(requestedType);
    }

    public TradeResult executeTrade(TradeOffer offer, ResourceType chosenOfferedType, Game game) {
        Player offerer = offer.offerer();
        Player target = offer.target();
        ResourceType requestedType = offer.requestedType();

        // Validate chosen type is in the offered types
        if (!offer.offeredTypes().contains(chosenOfferedType)) {
            return TradeResult.declined("Invalid choice: " + chosenOfferedType + " was not offered");
        }

        // Transfer resources
        offerer.getHand().remove(chosenOfferedType);
        target.getHand().add(chosenOfferedType);

        target.getHand().remove(requestedType);
        offerer.getHand().add(requestedType);

        // Target draws bonus card
        ResourceType bonusCard = game.getResourceDeck().draw();
        target.getHand().add(bonusCard);

        return TradeResult.accepted(chosenOfferedType, requestedType);
    }
}
