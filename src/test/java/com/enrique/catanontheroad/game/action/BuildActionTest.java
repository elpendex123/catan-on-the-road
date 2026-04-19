package com.enrique.catanontheroad.game.action;

import com.enrique.catanontheroad.game.Game;
import com.enrique.catanontheroad.game.Player;
import com.enrique.catanontheroad.game.card.MetropolisSide;
import com.enrique.catanontheroad.game.card.MetropolisType;
import com.enrique.catanontheroad.game.card.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BuildActionTest {

    private Game game;
    private Player player;
    private BuildAction buildAction;

    @BeforeEach
    void setUp() {
        game = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);
        player = game.getCurrentPlayer();
        buildAction = new BuildAction();
    }

    @Test
    void can_afford_road_with_sufficient_resources() {
        player.getHand().add(ResourceType.BRICK);
        player.getHand().add(ResourceType.WOOD);

        var result = buildAction.canAffordRoad(player);

        assertThat(result.affordable()).isTrue();
    }

    @Test
    void cannot_afford_road_without_resources() {
        // Clear hand
        for (ResourceType type : ResourceType.values()) {
            while (player.getHand().has(type)) {
                player.getHand().remove(type);
            }
        }

        var result = buildAction.canAffordRoad(player);

        assertThat(result.affordable()).isFalse();
        assertThat(result.reason()).containsAnyOf("brick", "wood");
    }

    @Test
    void can_afford_settlement_with_sufficient_resources() {
        player.getHand().add(ResourceType.BRICK);
        player.getHand().add(ResourceType.WOOD);
        player.getHand().add(ResourceType.WOOL);
        player.getHand().add(ResourceType.WHEAT);

        var result = buildAction.canAffordSettlement(player);

        assertThat(result.affordable()).isTrue();
    }

    @Test
    void can_afford_city_with_settlement_and_resources() {
        player.getHand().add(ResourceType.WHEAT, 2);
        player.getHand().add(ResourceType.ORE, 3);
        // Player already has starting settlement

        var result = buildAction.canAffordCity(player);

        assertThat(result.affordable()).isTrue();
    }

    @Test
    void cannot_afford_city_without_settlement() {
        // Build city to cover the starting settlement
        player.getHand().add(ResourceType.WHEAT, 4);
        player.getHand().add(ResourceType.ORE, 6);
        buildAction.buildCity(player, game);

        var result = buildAction.canAffordCity(player);

        assertThat(result.affordable()).isFalse();
        assertThat(result.reason()).contains("settlement");
    }

    @Test
    void can_afford_knight_with_sufficient_resources() {
        player.getHand().add(ResourceType.WOOL);
        player.getHand().add(ResourceType.ORE);

        var result = buildAction.canAffordKnight(player);

        assertThat(result.affordable()).isTrue();
    }

    @Test
    void can_afford_metropolis_with_city_and_resources() {
        // Build city first
        player.getHand().add(ResourceType.WHEAT, 2);
        player.getHand().add(ResourceType.ORE, 3);
        buildAction.buildCity(player, game);

        // Add metropolis resources
        player.getHand().add(ResourceType.WOOL, 3);
        player.getHand().add(ResourceType.ORE, 1);

        var result = buildAction.canAffordMetropolis(player, game);

        assertThat(result.affordable()).isTrue();
    }

    @Test
    void cannot_afford_metropolis_without_city() {
        player.getHand().add(ResourceType.WOOL, 3);
        player.getHand().add(ResourceType.ORE, 1);

        var result = buildAction.canAffordMetropolis(player, game);

        assertThat(result.affordable()).isFalse();
        assertThat(result.reason()).contains("city");
    }

    @Test
    void build_road_should_add_road_and_pay_cost() {
        // Clear hand first
        for (ResourceType type : ResourceType.values()) {
            while (player.getHand().has(type)) {
                player.getHand().remove(type);
            }
        }
        player.getHand().add(ResourceType.BRICK);
        player.getHand().add(ResourceType.WOOD);
        int roadsBefore = player.getRoadCount();

        var result = buildAction.buildRoad(player, game);

        assertThat(result.success()).isTrue();
        assertThat(player.getRoadCount()).isEqualTo(roadsBefore + 1);
        assertThat(player.getHand().has(ResourceType.BRICK)).isFalse();
        assertThat(player.getHand().has(ResourceType.WOOD)).isFalse();
    }

    @Test
    void build_settlement_should_add_settlement_and_pay_cost() {
        player.getHand().add(ResourceType.BRICK);
        player.getHand().add(ResourceType.WOOD);
        player.getHand().add(ResourceType.WOOL);
        player.getHand().add(ResourceType.WHEAT);
        int settlementsBefore = player.getUncoveredSettlementCount();

        var result = buildAction.buildSettlement(player, game);

        assertThat(result.success()).isTrue();
        assertThat(player.getUncoveredSettlementCount()).isEqualTo(settlementsBefore + 1);
    }

    @Test
    void build_city_should_cover_settlement_and_add_city() {
        player.getHand().add(ResourceType.WHEAT, 2);
        player.getHand().add(ResourceType.ORE, 3);
        int settlementsBefore = player.getUncoveredSettlementCount();

        var result = buildAction.buildCity(player, game);

        assertThat(result.success()).isTrue();
        assertThat(player.getUncoveredSettlementCount()).isEqualTo(settlementsBefore - 1);
        assertThat(player.getUncoveredCityCount()).isEqualTo(1);
    }

    @Test
    void build_knight_should_add_knight_and_pay_cost() {
        player.getHand().add(ResourceType.WOOL);
        player.getHand().add(ResourceType.ORE);
        int knightsBefore = player.getKnightCount();

        var result = buildAction.buildKnight(player, game);

        assertThat(result.success()).isTrue();
        assertThat(player.getKnightCount()).isEqualTo(knightsBefore + 1);
    }

    @Test
    void build_metropolis_should_cover_city_and_add_metropolis() {
        // Build city first
        player.getHand().add(ResourceType.WHEAT, 2);
        player.getHand().add(ResourceType.ORE, 3);
        buildAction.buildCity(player, game);

        // Build metropolis
        player.getHand().add(ResourceType.WOOL, 3);
        player.getHand().add(ResourceType.ORE, 1);
        var metropolis = game.getMetropolisStack().take(MetropolisType.ROAD);

        var result = buildAction.buildMetropolis(player, game, metropolis);

        assertThat(result.success()).isTrue();
        assertThat(player.getUncoveredCityCount()).isZero();
        assertThat(player.getMetropolisCount()).isEqualTo(1);
    }

    @Test
    void build_road_should_fail_without_resources() {
        // Clear hand
        for (ResourceType type : ResourceType.values()) {
            while (player.getHand().has(type)) {
                player.getHand().remove(type);
            }
        }

        var result = buildAction.buildRoad(player, game);

        assertThat(result.success()).isFalse();
    }

    @Test
    void building_road_should_update_longest_route() {
        // Manually add roads to reach 3+ (since building row availability is random)
        player.addRoad();
        player.addRoad(); // Now has 3 roads total (1 starting + 2 added)

        game.updateBonuses();

        assertThat(player.getRoadCount()).isEqualTo(3);
        assertThat(player.hasLongestRoute()).isTrue();
    }

    private static org.assertj.core.api.Condition<String> contains(String text) {
        return new org.assertj.core.api.Condition<>(s -> s.contains(text), "contains " + text);
    }
}
