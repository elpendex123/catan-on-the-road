package com.enrique.catanontheroad.game.card;

import java.util.Map;

public sealed interface BuildingCard permits BuildingCard.Road, BuildingCard.Settlement, BuildingCard.City, BuildingCard.Knight {

    BuildingType type();

    Map<ResourceType, Integer> cost();

    int victoryPoints();

    record Road() implements BuildingCard {
        @Override
        public BuildingType type() {
            return BuildingType.ROAD;
        }

        @Override
        public Map<ResourceType, Integer> cost() {
            return Map.of(
                ResourceType.BRICK, 1,
                ResourceType.WOOD, 1
            );
        }

        @Override
        public int victoryPoints() {
            return 0;
        }
    }

    record Settlement() implements BuildingCard {
        @Override
        public BuildingType type() {
            return BuildingType.SETTLEMENT;
        }

        @Override
        public Map<ResourceType, Integer> cost() {
            return Map.of(
                ResourceType.BRICK, 1,
                ResourceType.WOOD, 1,
                ResourceType.WOOL, 1,
                ResourceType.WHEAT, 1
            );
        }

        @Override
        public int victoryPoints() {
            return 1;
        }
    }

    record City() implements BuildingCard {
        @Override
        public BuildingType type() {
            return BuildingType.CITY;
        }

        @Override
        public Map<ResourceType, Integer> cost() {
            return Map.of(
                ResourceType.WHEAT, 2,
                ResourceType.ORE, 3
            );
        }

        @Override
        public int victoryPoints() {
            return 2;
        }
    }

    record Knight() implements BuildingCard {
        @Override
        public BuildingType type() {
            return BuildingType.KNIGHT;
        }

        @Override
        public Map<ResourceType, Integer> cost() {
            return Map.of(
                ResourceType.WOOL, 1,
                ResourceType.ORE, 1
            );
        }

        @Override
        public int victoryPoints() {
            return 0;
        }
    }
}
