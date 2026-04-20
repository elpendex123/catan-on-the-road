package com.enrique.catanontheroad.shell.display;

import com.enrique.catanontheroad.game.Player;
import com.enrique.catanontheroad.game.card.ResourceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HandRendererTest {

    private HandRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = new HandRenderer();
    }

    @Test
    void should_render_hand_with_counts() {
        Player player = new Player("Alice");
        player.getHand().add(ResourceType.BRICK, 2);
        player.getHand().add(ResourceType.WHEAT, 3);

        String result = renderer.render(player);

        assertThat(result).contains("Alice's hand");
        assertThat(result).contains("x2");
        assertThat(result).contains("x3");
        assertThat(result).contains("Total: 5 cards");
    }

    @Test
    void should_render_all_resource_types() {
        Player player = new Player("Bob");
        player.getHand().add(ResourceType.BRICK);

        String result = renderer.render(player);

        assertThat(result).contains("1.").contains("2.").contains("3.").contains("4.").contains("5.");
    }

    @Test
    void should_render_final_hand_with_resources() {
        Player player = new Player("Carol");
        player.getHand().add(ResourceType.BRICK, 3);
        player.getHand().add(ResourceType.ORE, 1);

        String result = renderer.renderFinalHand(player);

        assertThat(result).contains("3");
        assertThat(result).contains("1");
    }

    @Test
    void should_render_empty_final_hand() {
        Player player = new Player("Dave");

        String result = renderer.renderFinalHand(player);

        assertThat(result).contains("(empty)");
    }
}
