package com.enrique.catanontheroad.game;

import com.enrique.catanontheroad.game.card.ResourceType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HandTest {

    @Test
    void new_hand_should_be_empty() {
        Hand hand = new Hand();

        assertThat(hand.total()).isZero();
        assertThat(hand.isEmpty()).isTrue();
        for (ResourceType type : ResourceType.values()) {
            assertThat(hand.count(type)).isZero();
        }
    }

    @Test
    void add_should_increase_count() {
        Hand hand = new Hand();

        hand.add(ResourceType.BRICK);

        assertThat(hand.count(ResourceType.BRICK)).isEqualTo(1);
        assertThat(hand.total()).isEqualTo(1);
        assertThat(hand.isEmpty()).isFalse();
    }

    @Test
    void add_with_count_should_increase_by_count() {
        Hand hand = new Hand();

        hand.add(ResourceType.WOOD, 3);

        assertThat(hand.count(ResourceType.WOOD)).isEqualTo(3);
    }

    @Test
    void add_negative_count_should_throw() {
        Hand hand = new Hand();

        assertThatThrownBy(() -> hand.add(ResourceType.BRICK, -1))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void add_list_should_add_all() {
        Hand hand = new Hand();

        hand.add(List.of(ResourceType.BRICK, ResourceType.BRICK, ResourceType.WOOD));

        assertThat(hand.count(ResourceType.BRICK)).isEqualTo(2);
        assertThat(hand.count(ResourceType.WOOD)).isEqualTo(1);
        assertThat(hand.total()).isEqualTo(3);
    }

    @Test
    void remove_should_decrease_count() {
        Hand hand = new Hand();
        hand.add(ResourceType.ORE, 3);

        hand.remove(ResourceType.ORE);

        assertThat(hand.count(ResourceType.ORE)).isEqualTo(2);
    }

    @Test
    void remove_with_count_should_decrease_by_count() {
        Hand hand = new Hand();
        hand.add(ResourceType.WHEAT, 5);

        hand.remove(ResourceType.WHEAT, 3);

        assertThat(hand.count(ResourceType.WHEAT)).isEqualTo(2);
    }

    @Test
    void remove_more_than_available_should_throw() {
        Hand hand = new Hand();
        hand.add(ResourceType.WOOL, 2);

        assertThatThrownBy(() -> hand.remove(ResourceType.WOOL, 3))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Cannot remove");
    }

    @Test
    void has_should_return_true_when_present() {
        Hand hand = new Hand();
        hand.add(ResourceType.BRICK);

        assertThat(hand.has(ResourceType.BRICK)).isTrue();
        assertThat(hand.has(ResourceType.WOOD)).isFalse();
    }

    @Test
    void has_with_count_should_check_minimum() {
        Hand hand = new Hand();
        hand.add(ResourceType.ORE, 3);

        assertThat(hand.has(ResourceType.ORE, 3)).isTrue();
        assertThat(hand.has(ResourceType.ORE, 4)).isFalse();
    }

    @Test
    void can_afford_should_check_all_resources() {
        Hand hand = new Hand();
        hand.add(ResourceType.BRICK, 2);
        hand.add(ResourceType.WOOD, 1);

        Map<ResourceType, Integer> affordableCost = Map.of(
            ResourceType.BRICK, 1,
            ResourceType.WOOD, 1
        );

        Map<ResourceType, Integer> unaffordableCost = Map.of(
            ResourceType.BRICK, 3
        );

        assertThat(hand.canAfford(affordableCost)).isTrue();
        assertThat(hand.canAfford(unaffordableCost)).isFalse();
    }

    @Test
    void pay_should_remove_resources() {
        Hand hand = new Hand();
        hand.add(ResourceType.BRICK, 2);
        hand.add(ResourceType.WOOD, 2);
        hand.add(ResourceType.WOOL, 2);
        hand.add(ResourceType.WHEAT, 2);

        Map<ResourceType, Integer> cost = Map.of(
            ResourceType.BRICK, 1,
            ResourceType.WOOD, 1,
            ResourceType.WOOL, 1,
            ResourceType.WHEAT, 1
        );

        hand.pay(cost);

        assertThat(hand.count(ResourceType.BRICK)).isEqualTo(1);
        assertThat(hand.count(ResourceType.WOOD)).isEqualTo(1);
        assertThat(hand.count(ResourceType.WOOL)).isEqualTo(1);
        assertThat(hand.count(ResourceType.WHEAT)).isEqualTo(1);
    }

    @Test
    void pay_unaffordable_should_throw() {
        Hand hand = new Hand();
        hand.add(ResourceType.BRICK, 1);

        Map<ResourceType, Integer> cost = Map.of(ResourceType.BRICK, 2);

        assertThatThrownBy(() -> hand.pay(cost))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Cannot afford");
    }

    @Test
    void get_cards_should_return_copy() {
        Hand hand = new Hand();
        hand.add(ResourceType.BRICK, 3);

        Map<ResourceType, Integer> cards = hand.getCards();
        cards.put(ResourceType.BRICK, 0); // Modify the copy

        assertThat(hand.count(ResourceType.BRICK)).isEqualTo(3); // Original unchanged
    }
}
