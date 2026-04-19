package com.enrique.catanontheroad.game.deck;

import com.enrique.catanontheroad.game.card.BuildingCard;
import com.enrique.catanontheroad.game.card.BuildingType;
import com.enrique.catanontheroad.game.rng.SeededRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BuildingDeck {

    private static final int ROAD_COUNT = 10;
    private static final int SETTLEMENT_COUNT = 11;
    private static final int CITY_COUNT = 5;
    private static final int KNIGHT_COUNT = 8;
    private static final int ROW_SIZE = 5;

    private final List<BuildingCard> drawPile;
    private final List<BuildingCard> discardPile;
    private final List<BuildingCard> buildingRow;
    private final SeededRandom rng;

    public BuildingDeck(SeededRandom rng) {
        this.rng = rng;
        this.drawPile = new ArrayList<>();
        this.discardPile = new ArrayList<>();
        this.buildingRow = new ArrayList<>();
        initializeDeck();
        initializeBuildingRow();
    }

    private void initializeDeck() {
        for (int i = 0; i < ROAD_COUNT; i++) {
            drawPile.add(new BuildingCard.Road());
        }
        for (int i = 0; i < SETTLEMENT_COUNT; i++) {
            drawPile.add(new BuildingCard.Settlement());
        }
        for (int i = 0; i < CITY_COUNT; i++) {
            drawPile.add(new BuildingCard.City());
        }
        for (int i = 0; i < KNIGHT_COUNT; i++) {
            drawPile.add(new BuildingCard.Knight());
        }
        rng.shuffle(drawPile);
    }

    private void initializeBuildingRow() {
        // Guarantee one of each type in initial row
        BuildingCard road = removeFirstOfType(BuildingType.ROAD);
        BuildingCard settlement = removeFirstOfType(BuildingType.SETTLEMENT);
        BuildingCard city = removeFirstOfType(BuildingType.CITY);
        BuildingCard knight = removeFirstOfType(BuildingType.KNIGHT);

        buildingRow.add(road);
        buildingRow.add(settlement);
        buildingRow.add(city);
        buildingRow.add(knight);

        // Add one more card from the draw pile
        if (!drawPile.isEmpty()) {
            buildingRow.add(drawPile.remove(drawPile.size() - 1));
        }

        rng.shuffle(buildingRow);
    }

    private BuildingCard removeFirstOfType(BuildingType type) {
        for (int i = 0; i < drawPile.size(); i++) {
            if (drawPile.get(i).type() == type) {
                return drawPile.remove(i);
            }
        }
        throw new IllegalStateException("No card of type " + type + " found in draw pile");
    }

    public List<BuildingCard> getBuildingRow() {
        return new ArrayList<>(buildingRow);
    }

    public BuildingCard takeFromRow(int index) {
        if (index < 0 || index >= buildingRow.size()) {
            throw new IllegalArgumentException("Invalid row index: " + index);
        }
        BuildingCard card = buildingRow.remove(index);
        refillRow();
        return card;
    }

    public BuildingCard takeFirstOfType(BuildingType type) {
        for (int i = 0; i < buildingRow.size(); i++) {
            if (buildingRow.get(i).type() == type) {
                return takeFromRow(i);
            }
        }
        throw new IllegalStateException("No card of type " + type + " in building row");
    }

    public boolean hasTypeInRow(BuildingType type) {
        return buildingRow.stream().anyMatch(card -> card.type() == type);
    }

    private void refillRow() {
        while (buildingRow.size() < ROW_SIZE && !drawPile.isEmpty()) {
            buildingRow.add(drawPile.remove(drawPile.size() - 1));
        }

        // If draw pile is empty and we need more cards, reshuffle discard pile
        if (buildingRow.size() < ROW_SIZE && !discardPile.isEmpty()) {
            reshuffleDiscardPile();
            while (buildingRow.size() < ROW_SIZE && !drawPile.isEmpty()) {
                buildingRow.add(drawPile.remove(drawPile.size() - 1));
            }
        }

        // Check for 5-of-a-kind
        checkAndHandleFiveOfAKind();
    }

    private void checkAndHandleFiveOfAKind() {
        if (buildingRow.size() == ROW_SIZE) {
            Set<BuildingType> types = buildingRow.stream()
                .map(BuildingCard::type)
                .collect(Collectors.toSet());

            if (types.size() == 1) {
                // All 5 cards are the same type - reshuffle
                drawPile.addAll(buildingRow);
                buildingRow.clear();
                rng.shuffle(drawPile);
                for (int i = 0; i < ROW_SIZE && !drawPile.isEmpty(); i++) {
                    buildingRow.add(drawPile.remove(drawPile.size() - 1));
                }
                // Recursively check again in case we drew another 5-of-a-kind
                checkAndHandleFiveOfAKind();
            }
        }
    }

    private void reshuffleDiscardPile() {
        drawPile.addAll(discardPile);
        discardPile.clear();
        rng.shuffle(drawPile);
    }

    public void discard(BuildingCard card) {
        discardPile.add(card);
    }

    public int drawPileSize() {
        return drawPile.size();
    }

    public int discardPileSize() {
        return discardPile.size();
    }

    public int rowSize() {
        return buildingRow.size();
    }
}
