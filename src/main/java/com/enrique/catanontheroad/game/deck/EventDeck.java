package com.enrique.catanontheroad.game.deck;

import com.enrique.catanontheroad.game.card.EventCard;
import com.enrique.catanontheroad.game.rng.SeededRandom;

import java.util.ArrayList;
import java.util.List;

public class EventDeck {

    private static final int ROBBER_COUNT = 3;

    private final List<EventCard> drawPile;
    private final List<EventCard> discardPile;
    private final SeededRandom rng;

    public EventDeck(SeededRandom rng) {
        this.rng = rng;
        this.drawPile = new ArrayList<>();
        this.discardPile = new ArrayList<>();
        initializeDeck();
    }

    private void initializeDeck() {
        for (int i = 0; i < ROBBER_COUNT; i++) {
            drawPile.add(new EventCard.Robber());
        }
        drawPile.add(new EventCard.Abundance());
        drawPile.add(new EventCard.Charity());
        drawPile.add(new EventCard.Solstice());
        drawPile.add(new EventCard.Subsidy());
        rng.shuffle(drawPile);
    }

    public EventCard draw() {
        if (drawPile.isEmpty()) {
            reshuffleAll();
        }
        if (drawPile.isEmpty()) {
            throw new IllegalStateException("Cannot draw: event deck is empty");
        }
        EventCard card = drawPile.remove(drawPile.size() - 1);
        discardPile.add(card);
        return card;
    }

    public void reshuffleAll() {
        drawPile.addAll(discardPile);
        discardPile.clear();
        // Also add back the standard event cards if somehow not present
        ensureAllCardsPresent();
        rng.shuffle(drawPile);
    }

    private void ensureAllCardsPresent() {
        // This rebuilds the deck to ensure exactly 7 cards
        // In normal play this shouldn't be needed, but ensures consistency
        int robberCount = 0;
        boolean hasAbundance = false;
        boolean hasCharity = false;
        boolean hasSolstice = false;
        boolean hasSubsidy = false;

        for (EventCard card : drawPile) {
            if (card instanceof EventCard.Robber) robberCount++;
            else if (card instanceof EventCard.Abundance) hasAbundance = true;
            else if (card instanceof EventCard.Charity) hasCharity = true;
            else if (card instanceof EventCard.Solstice) hasSolstice = true;
            else if (card instanceof EventCard.Subsidy) hasSubsidy = true;
        }

        while (robberCount < ROBBER_COUNT) {
            drawPile.add(new EventCard.Robber());
            robberCount++;
        }
        if (!hasAbundance) drawPile.add(new EventCard.Abundance());
        if (!hasCharity) drawPile.add(new EventCard.Charity());
        if (!hasSolstice) drawPile.add(new EventCard.Solstice());
        if (!hasSubsidy) drawPile.add(new EventCard.Subsidy());
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
