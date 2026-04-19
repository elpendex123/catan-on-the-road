package com.enrique.catanontheroad.game.deck;

import com.enrique.catanontheroad.game.card.EventCard;
import com.enrique.catanontheroad.game.rng.SeededRandom;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventDeckTest {

    @Test
    void should_have_7_cards_at_start() {
        SeededRandom rng = new SeededRandom(42L);
        EventDeck deck = new EventDeck(rng);

        assertThat(deck.totalCards()).isEqualTo(7);
        assertThat(deck.drawPileSize()).isEqualTo(7);
        assertThat(deck.discardPileSize()).isZero();
    }

    @Test
    void draw_should_move_card_to_discard() {
        SeededRandom rng = new SeededRandom(42L);
        EventDeck deck = new EventDeck(rng);

        EventCard card = deck.draw();

        assertThat(card).isNotNull();
        assertThat(deck.drawPileSize()).isEqualTo(6);
        assertThat(deck.discardPileSize()).isEqualTo(1);
    }

    @Test
    void should_have_correct_card_distribution() {
        SeededRandom rng = new SeededRandom(42L);
        EventDeck deck = new EventDeck(rng);

        int robberCount = 0;
        int abundanceCount = 0;
        int charityCount = 0;
        int solsticeCount = 0;
        int subsidyCount = 0;

        for (int i = 0; i < 7; i++) {
            EventCard card = deck.draw();
            if (card instanceof EventCard.Robber) robberCount++;
            else if (card instanceof EventCard.Abundance) abundanceCount++;
            else if (card instanceof EventCard.Charity) charityCount++;
            else if (card instanceof EventCard.Solstice) solsticeCount++;
            else if (card instanceof EventCard.Subsidy) subsidyCount++;
        }

        assertThat(robberCount).isEqualTo(3);
        assertThat(abundanceCount).isEqualTo(1);
        assertThat(charityCount).isEqualTo(1);
        assertThat(solsticeCount).isEqualTo(1);
        assertThat(subsidyCount).isEqualTo(1);
    }

    @Test
    void should_auto_reshuffle_when_draw_pile_empty() {
        SeededRandom rng = new SeededRandom(42L);
        EventDeck deck = new EventDeck(rng);

        // Draw all 7 cards
        for (int i = 0; i < 7; i++) {
            deck.draw();
        }

        assertThat(deck.drawPileSize()).isZero();
        assertThat(deck.discardPileSize()).isEqualTo(7);

        // Next draw should trigger auto-reshuffle
        EventCard card = deck.draw();

        assertThat(card).isNotNull();
        assertThat(deck.drawPileSize()).isEqualTo(6);
        assertThat(deck.discardPileSize()).isEqualTo(1);
    }

    @Test
    void reshuffle_all_should_combine_piles() {
        SeededRandom rng = new SeededRandom(42L);
        EventDeck deck = new EventDeck(rng);

        // Draw some cards
        deck.draw();
        deck.draw();
        deck.draw();

        assertThat(deck.drawPileSize()).isEqualTo(4);
        assertThat(deck.discardPileSize()).isEqualTo(3);

        deck.reshuffleAll();

        assertThat(deck.drawPileSize()).isEqualTo(7);
        assertThat(deck.discardPileSize()).isZero();
    }

    @Test
    void should_be_deterministic_with_same_seed() {
        SeededRandom rng1 = new SeededRandom(12345L);
        SeededRandom rng2 = new SeededRandom(12345L);
        EventDeck deck1 = new EventDeck(rng1);
        EventDeck deck2 = new EventDeck(rng2);

        for (int i = 0; i < 7; i++) {
            EventCard card1 = deck1.draw();
            EventCard card2 = deck2.draw();
            assertThat(card1.name()).isEqualTo(card2.name());
        }
    }
}
