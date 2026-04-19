package com.enrique.catanontheroad.game.action;

import com.enrique.catanontheroad.game.Game;
import com.enrique.catanontheroad.game.Player;
import com.enrique.catanontheroad.game.card.BuildingCard;
import com.enrique.catanontheroad.game.card.BuildingType;
import com.enrique.catanontheroad.game.card.Metropolis;
import com.enrique.catanontheroad.game.card.ResourceType;

import java.util.Map;

public class BuildAction {

    public record BuildResult(boolean success, String message) {
        public static BuildResult success(String message) {
            return new BuildResult(true, message);
        }
        public static BuildResult failure(String message) {
            return new BuildResult(false, message);
        }
    }

    public record AffordabilityCheck(boolean affordable, String reason) {
        public static AffordabilityCheck yes() {
            return new AffordabilityCheck(true, "affordable");
        }
        public static AffordabilityCheck no(String reason) {
            return new AffordabilityCheck(false, reason);
        }
    }

    public AffordabilityCheck canAffordRoad(Player player) {
        Map<ResourceType, Integer> cost = new BuildingCard.Road().cost();
        return checkAffordability(player, cost);
    }

    public AffordabilityCheck canAffordSettlement(Player player) {
        Map<ResourceType, Integer> cost = new BuildingCard.Settlement().cost();
        return checkAffordability(player, cost);
    }

    public AffordabilityCheck canAffordCity(Player player) {
        if (!player.hasUncoveredSettlement()) {
            return AffordabilityCheck.no("need uncovered settlement");
        }
        Map<ResourceType, Integer> cost = new BuildingCard.City().cost();
        return checkAffordability(player, cost);
    }

    public AffordabilityCheck canAffordKnight(Player player) {
        Map<ResourceType, Integer> cost = new BuildingCard.Knight().cost();
        return checkAffordability(player, cost);
    }

    public AffordabilityCheck canAffordMetropolis(Player player, Game game) {
        if (game.getMetropolisStack().isEmpty()) {
            return AffordabilityCheck.no("no metropolises available");
        }
        if (!player.hasUncoveredCity()) {
            return AffordabilityCheck.no("need uncovered city");
        }
        return checkAffordability(player, Metropolis.COST);
    }

    private AffordabilityCheck checkAffordability(Player player, Map<ResourceType, Integer> cost) {
        for (Map.Entry<ResourceType, Integer> entry : cost.entrySet()) {
            int have = player.getHand().count(entry.getKey());
            int need = entry.getValue();
            if (have < need) {
                return AffordabilityCheck.no("need " + (need - have) + " more " + entry.getKey().name().toLowerCase());
            }
        }
        return AffordabilityCheck.yes();
    }

    public BuildResult buildRoad(Player player, Game game) {
        AffordabilityCheck check = canAffordRoad(player);
        if (!check.affordable()) {
            return BuildResult.failure(check.reason());
        }

        if (!game.getBuildingDeck().hasTypeInRow(BuildingType.ROAD)) {
            return BuildResult.failure("no road available in building row");
        }

        BuildingCard card = game.getBuildingDeck().takeFirstOfType(BuildingType.ROAD);
        player.getHand().pay(card.cost());
        player.addRoad();
        game.updateBonuses();

        return BuildResult.success("Built road");
    }

    public BuildResult buildSettlement(Player player, Game game) {
        AffordabilityCheck check = canAffordSettlement(player);
        if (!check.affordable()) {
            return BuildResult.failure(check.reason());
        }

        if (!game.getBuildingDeck().hasTypeInRow(BuildingType.SETTLEMENT)) {
            return BuildResult.failure("no settlement available in building row");
        }

        BuildingCard card = game.getBuildingDeck().takeFirstOfType(BuildingType.SETTLEMENT);
        player.getHand().pay(card.cost());
        player.addSettlement();

        return BuildResult.success("Built settlement");
    }

    public BuildResult buildCity(Player player, Game game) {
        AffordabilityCheck check = canAffordCity(player);
        if (!check.affordable()) {
            return BuildResult.failure(check.reason());
        }

        if (!game.getBuildingDeck().hasTypeInRow(BuildingType.CITY)) {
            return BuildResult.failure("no city available in building row");
        }

        BuildingCard card = game.getBuildingDeck().takeFirstOfType(BuildingType.CITY);
        player.getHand().pay(card.cost());
        player.addCity();

        return BuildResult.success("Built city");
    }

    public BuildResult buildKnight(Player player, Game game) {
        AffordabilityCheck check = canAffordKnight(player);
        if (!check.affordable()) {
            return BuildResult.failure(check.reason());
        }

        if (!game.getBuildingDeck().hasTypeInRow(BuildingType.KNIGHT)) {
            return BuildResult.failure("no knight available in building row");
        }

        BuildingCard card = game.getBuildingDeck().takeFirstOfType(BuildingType.KNIGHT);
        player.getHand().pay(card.cost());
        player.addKnight();
        game.updateBonuses();

        return BuildResult.success("Built knight");
    }

    public BuildResult buildMetropolis(Player player, Game game, Metropolis metropolis) {
        AffordabilityCheck check = canAffordMetropolis(player, game);
        if (!check.affordable()) {
            return BuildResult.failure(check.reason());
        }

        player.getHand().pay(Metropolis.COST);
        player.addMetropolis(metropolis);
        game.updateBonuses();

        return BuildResult.success("Built " + metropolis.type().name().toLowerCase() + " metropolis");
    }
}
