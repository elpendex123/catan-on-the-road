package com.enrique.catanontheroad.shell.display;

import com.enrique.catanontheroad.game.Game;
import com.enrique.catanontheroad.game.Player;
import com.enrique.catanontheroad.game.card.MetropolisSide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BoardRendererTest {

    private BoardRenderer renderer;
    private Game game;

    @BeforeEach
    void setUp() {
        renderer = new BoardRenderer();
        game = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);
    }

    @Test
    void should_render_building_row_with_box() {
        String result = renderer.renderBuildingRow(game);

        assertThat(result).contains("BUILDING ROW");
        assertThat(result).contains("[1]");
        assertThat(result).contains("╔").contains("╚");
    }

    @Test
    void should_render_player_areas() {
        String result = renderer.renderPlayerAreas(game);

        assertThat(result).contains("Alice");
        assertThat(result).contains("Bob");
        assertThat(result).contains("Carol");
        assertThat(result).contains("Roads:");
        assertThat(result).contains("Settlements:");
        assertThat(result).contains("Hand size:");
        assertThat(result).contains("Longest Route").contains("Largest Army");
    }

    @Test
    void should_show_longest_route_marker() {
        Player alice = game.getPlayerByName("Alice");
        alice.setLongestRoute(true);

        String result = renderer.renderPlayerAreas(game);

        assertThat(result).contains("*");
    }

    @Test
    void should_show_largest_army_marker() {
        Player bob = game.getPlayerByName("Bob");
        bob.setLargestArmy(true);

        String result = renderer.renderPlayerAreas(game);

        assertThat(result).contains("+");
    }
}
