package com.enrique.catanontheroad.game.action;

import com.enrique.catanontheroad.game.Game;
import com.enrique.catanontheroad.game.Player;
import com.enrique.catanontheroad.game.card.MetropolisSide;
import com.enrique.catanontheroad.game.card.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SubstituteActionTest {

    private Game game;
    private Player player;
    private SubstituteAction substituteAction;

    @BeforeEach
    void setUp() {
        game = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);
        player = game.getCurrentPlayer();
        substituteAction = new SubstituteAction();

        // Clear hand
        for (ResourceType type : ResourceType.values()) {
            while (player.getHand().has(type)) {
                player.getHand().remove(type);
            }
        }
    }

    @Test
    void get_ratio_with_1_road_should_be_4_to_1() {
        // Player starts with 1 road
        assertThat(player.getRoadCount()).isEqualTo(1);
        assertThat(substituteAction.getRatio(player)).isEqualTo(4);
    }

    @Test
    void get_ratio_with_2_roads_should_be_3_to_1() {
        player.addRoad();
        assertThat(player.getRoadCount()).isEqualTo(2);
        assertThat(substituteAction.getRatio(player)).isEqualTo(3);
    }

    @Test
    void get_ratio_with_3_roads_should_be_2_to_1() {
        player.addRoad();
        player.addRoad();
        assertThat(player.getRoadCount()).isEqualTo(3);
        assertThat(substituteAction.getRatio(player)).isEqualTo(2);
    }

    @Test
    void get_ratio_with_4_plus_roads_should_be_1_to_1() {
        player.addRoad();
        player.addRoad();
        player.addRoad();
        assertThat(player.getRoadCount()).isEqualTo(4);
        assertThat(substituteAction.getRatio(player)).isEqualTo(1);
    }

    @Test
    void can_substitute_should_pass_with_sufficient_resources() {
        player.getHand().add(ResourceType.BRICK, 4);

        var result = substituteAction.canSubstitute(player, ResourceType.BRICK, ResourceType.WOOD);

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void can_substitute_should_fail_with_insufficient_resources() {
        player.getHand().add(ResourceType.BRICK, 3);

        var result = substituteAction.canSubstitute(player, ResourceType.BRICK, ResourceType.WOOD);

        assertThat(result.isValid()).isFalse();
        assertThat(result.message()).contains("4");
        assertThat(result.message()).contains("3");
    }

    @Test
    void can_substitute_should_fail_when_source_equals_target() {
        player.getHand().add(ResourceType.BRICK, 4);

        var result = substituteAction.canSubstitute(player, ResourceType.BRICK, ResourceType.BRICK);

        assertThat(result.isValid()).isFalse();
        assertThat(result.message()).contains("different");
    }

    @Test
    void execute_should_remove_source_and_add_target() {
        player.getHand().add(ResourceType.BRICK, 4);

        var result = substituteAction.execute(player, ResourceType.BRICK, ResourceType.WOOD, game);

        assertThat(result.success()).isTrue();
        assertThat(player.getHand().count(ResourceType.BRICK)).isZero();
        assertThat(player.getHand().count(ResourceType.WOOD)).isEqualTo(1);
    }

    @Test
    void execute_with_better_ratio_should_remove_fewer_cards() {
        player.addRoad();
        player.addRoad(); // 3 roads total = 2:1 ratio
        player.getHand().add(ResourceType.WOOL, 2);

        var result = substituteAction.execute(player, ResourceType.WOOL, ResourceType.ORE, game);

        assertThat(result.success()).isTrue();
        assertThat(player.getHand().count(ResourceType.WOOL)).isZero();
        assertThat(player.getHand().count(ResourceType.ORE)).isEqualTo(1);
    }

    @Test
    void execute_with_1_to_1_ratio() {
        player.addRoad();
        player.addRoad();
        player.addRoad(); // 4 roads = 1:1 ratio
        player.getHand().add(ResourceType.WHEAT, 1);

        var result = substituteAction.execute(player, ResourceType.WHEAT, ResourceType.ORE, game);

        assertThat(result.success()).isTrue();
        assertThat(player.getHand().count(ResourceType.WHEAT)).isZero();
        assertThat(player.getHand().count(ResourceType.ORE)).isEqualTo(1);
    }

    @Test
    void execute_should_fail_with_invalid_params() {
        var result = substituteAction.execute(player, ResourceType.BRICK, ResourceType.WOOD, game);

        assertThat(result.success()).isFalse();
    }

    private static org.assertj.core.api.Condition<String> contains(String text) {
        return new org.assertj.core.api.Condition<>(s -> s.contains(text), "contains " + text);
    }
}
