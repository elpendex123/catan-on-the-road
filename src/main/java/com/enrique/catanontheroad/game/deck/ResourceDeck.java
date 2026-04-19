package com.enrique.catanontheroad.game.deck;

import com.enrique.catanontheroad.game.card.ResourceType;
import com.enrique.catanontheroad.game.rng.SeededRandom;

import java.util.ArrayList;
import java.util.List;

public class ResourceDeck {

    private static final int BRICK_COUNT = 11;
    private static final int WOOD_COUNT = 11;
    private static final int WOOL_COUNT = 14;
    private static final int WHEAT_COUNT = 12;
    private static final int ORE_COUNT = 13;

    private final List<ResourceType> drawPile;
    private final List<ResourceType> discardPile;
    private final SeededRandom rng;

    public ResourceDeck(SeededRandom rng) {
        this.rng = rng;
        this.drawPile = new ArrayList<>();
        this.discardPile = new ArrayList<>();
        initializeDeck();
    }

    private void initializeDeck() {
        for (int i = 0; i < BRICK_COUNT; i++) {
            drawPile.add(ResourceType.BRICK);
        }
        for (int i = 0; i < WOOD_COUNT; i++) {
            drawPile.add(ResourceType.WOOD);
        }
        for (int i = 0; i < WOOL_COUNT; i++) {
            drawPile.add(ResourceType.WOOL);
        }
        for (int i = 0; i < WHEAT_COUNT; i++) {
            drawPile.add(ResourceType.WHEAT);
        }
        for (int i = 0; i < ORE_COUNT; i++) {
            drawPile.add(ResourceType.ORE);
        }
        rng.shuffle(drawPile);
    }

    public ResourceType draw() {
        if (drawPile.isEmpty()) {
            reshuffleDiscardPile();
        }
        if (drawPile.isEmpty()) {
            throw new IllegalStateException("Cannot draw: both draw pile and discard pile are empty");
        }
        return drawPile.remove(drawPile.size() - 1);
    }

    public List<ResourceType> draw(int count) {
        List<ResourceType> drawn = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            drawn.add(draw());
        }
        return drawn;
    }

    public void discard(ResourceType card) {
        discardPile.add(card);
    }

    public void discard(List<ResourceType> cards) {
        discardPile.addAll(cards);
    }

    private void reshuffleDiscardPile() {
        drawPile.addAll(discardPile);
        discardPile.clear();
        rng.shuffle(drawPile);
    }

    public int drawPileSize() {
        return drawPile.size();
    }

    public int discardPileSize() {
        return discardPile.size();
    }

    public int totalCards() {
        return drawPile.size() + discardPile.size();
    }
}
