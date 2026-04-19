package com.enrique.catanontheroad.game.deck;

import com.enrique.catanontheroad.game.card.ResourceType;
import com.enrique.catanontheroad.game.rng.SeededRandom;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResourceDeckTest {

    @Test
    void should_have_61_cards_at_start() {
        SeededRandom rng = new SeededRandom(42L);
        ResourceDeck deck = new ResourceDeck(rng);

        assertThat(deck.totalCards()).isEqualTo(61);
        assertThat(deck.drawPileSize()).isEqualTo(61);
        assertThat(deck.discardPileSize()).isZero();
    }

    @Test
    void should_have_correct_card_distribution() {
        SeededRandom rng = new SeededRandom(42L);
        ResourceDeck deck = new ResourceDeck(rng);

        Map<ResourceType, Integer> counts = new EnumMap<>(ResourceType.class);
        for (ResourceType type : ResourceType.values()) {
            counts.put(type, 0);
        }

        for (int i = 0; i < 61; i++) {
            ResourceType card = deck.draw();
            counts.merge(card, 1, Integer::sum);
        }

        assertThat(counts.get(ResourceType.BRICK)).isEqualTo(11);
        assertThat(counts.get(ResourceType.WOOD)).isEqualTo(11);
        assertThat(counts.get(ResourceType.WOOL)).isEqualTo(14);
        assertThat(counts.get(ResourceType.WHEAT)).isEqualTo(12);
        assertThat(counts.get(ResourceType.ORE)).isEqualTo(13);
    }

    @Test
    void draw_should_reduce_pile_size() {
        SeededRandom rng = new SeededRandom(42L);
        ResourceDeck deck = new ResourceDeck(rng);

        deck.draw();

        assertThat(deck.drawPileSize()).isEqualTo(60);
    }

    @Test
    void draw_multiple_should_return_correct_count() {
        SeededRandom rng = new SeededRandom(42L);
        ResourceDeck deck = new ResourceDeck(rng);

        List<ResourceType> drawn = deck.draw(5);

        assertThat(drawn).hasSize(5);
        assertThat(deck.drawPileSize()).isEqualTo(56);
    }

    @Test
    void discard_should_add_to_discard_pile() {
        SeededRandom rng = new SeededRandom(42L);
        ResourceDeck deck = new ResourceDeck(rng);

        deck.discard(ResourceType.BRICK);

        assertThat(deck.discardPileSize()).isEqualTo(1);
        assertThat(deck.totalCards()).isEqualTo(62); // 61 + 1 discarded
    }

    @Test
    void discard_list_should_add_all_to_discard_pile() {
        SeededRandom rng = new SeededRandom(42L);
        ResourceDeck deck = new ResourceDeck(rng);

        deck.discard(List.of(ResourceType.BRICK, ResourceType.WOOD, ResourceType.WOOL));

        assertThat(deck.discardPileSize()).isEqualTo(3);
    }

    @Test
    void should_reshuffle_discard_when_draw_pile_empty() {
        SeededRandom rng = new SeededRandom(42L);
        ResourceDeck deck = new ResourceDeck(rng);

        // Draw all cards
        for (int i = 0; i < 61; i++) {
            ResourceType card = deck.draw();
            deck.discard(card);
        }

        assertThat(deck.drawPileSize()).isZero();
        assertThat(deck.discardPileSize()).isEqualTo(61);

        // Next draw should trigger reshuffle
        ResourceType card = deck.draw();

        assertThat(card).isNotNull();
        assertThat(deck.drawPileSize()).isEqualTo(60);
        assertThat(deck.discardPileSize()).isZero();
    }

    @Test
    void should_throw_when_both_piles_empty() {
        SeededRandom rng = new SeededRandom(42L);
        ResourceDeck deck = new ResourceDeck(rng);

        // Draw all cards without discarding
        for (int i = 0; i < 61; i++) {
            deck.draw();
        }

        assertThatThrownBy(deck::draw)
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("empty");
    }

    @Test
    void should_be_deterministic_with_same_seed() {
        SeededRandom rng1 = new SeededRandom(12345L);
        SeededRandom rng2 = new SeededRandom(12345L);
        ResourceDeck deck1 = new ResourceDeck(rng1);
        ResourceDeck deck2 = new ResourceDeck(rng2);

        for (int i = 0; i < 10; i++) {
            assertThat(deck1.draw()).isEqualTo(deck2.draw());
        }
    }
}
