package com.enrique.catanontheroad.shell.display;

import com.enrique.catanontheroad.game.Game;
import com.enrique.catanontheroad.game.Player;
import com.enrique.catanontheroad.game.card.MetropolisSide;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerAreaRendererTest {

    @Test
    void should_render_turn_header() {
        Game game = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);
        PlayerAreaRenderer renderer = new PlayerAreaRenderer();

        String result = renderer.renderTurnHeader(game);

        assertThat(result).contains("'s turn");
        assertThat(result).contains("Round 1");
        assertThat(result).contains("VPs:");
        assertThat(result).contains("Hand size:");
        assertThat(result).contains("Roads:");
        assertThat(result).contains("Knights:");
    }

    @Test
    void should_show_bonus_holders() {
        Game game = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);
        // Give Alice enough roads for longest route
        Player alice = game.getPlayerByName("Alice");
        alice.addRoad();
        alice.addRoad();
        game.updateBonuses();

        PlayerAreaRenderer renderer = new PlayerAreaRenderer();
        String result = renderer.renderTurnHeader(game);

        assertThat(result).contains("Longest Route:");
    }

    @Test
    void should_show_no_holder_when_none() {
        Game game = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);
        PlayerAreaRenderer renderer = new PlayerAreaRenderer();

        String result = renderer.renderTurnHeader(game);

        assertThat(result).contains("— (no holder)");
    }

    @Test
    void should_show_largest_army_holder() {
        Game game = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);
        Player alice = game.getPlayerByName("Alice");
        alice.addKnight();
        alice.addKnight();
        game.updateBonuses();

        PlayerAreaRenderer renderer = new PlayerAreaRenderer();
        String result = renderer.renderTurnHeader(game);

        assertThat(result).contains("Alice");
        assertThat(result).contains("knights");
    }
}
