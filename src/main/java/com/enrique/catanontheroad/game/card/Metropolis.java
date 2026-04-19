package com.enrique.catanontheroad.game.card;

import java.util.Map;

public record Metropolis(MetropolisSide side, MetropolisType type) {

    public static final Map<ResourceType, Integer> COST = Map.of(
        ResourceType.WOOL, 3,
        ResourceType.ORE, 1
    );

    public static final int VICTORY_POINTS = 3;

    public int harvestBonus() {
        return side == MetropolisSide.A ? 2 : 1;
    }

    public boolean hasTieBreakForLongestRoute() {
        return side == MetropolisSide.B && type == MetropolisType.LONGEST_ROUTE;
    }

    public boolean hasTieBreakForLargestArmy() {
        return side == MetropolisSide.B && type == MetropolisType.LARGEST_ARMY;
    }

    public boolean hasOnBuildDrawForRoads() {
        return side == MetropolisSide.B && type == MetropolisType.ROAD;
    }

    public boolean hasOnBuildDrawForKnights() {
        return side == MetropolisSide.B && type == MetropolisType.KNIGHT;
    }
}
