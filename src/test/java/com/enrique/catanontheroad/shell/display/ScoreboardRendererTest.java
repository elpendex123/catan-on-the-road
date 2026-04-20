package com.enrique.catanontheroad.shell.display;

import com.enrique.catanontheroad.game.Game;
import com.enrique.catanontheroad.game.Player;
import com.enrique.catanontheroad.game.card.MetropolisSide;
import com.enrique.catanontheroad.game.card.ResourceType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ScoreboardRendererTest {

    @Test
    void should_render_scoreboard_with_all_players() {
        Game game = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);
        ScoreboardRenderer renderer = new ScoreboardRenderer();

        String result = renderer.render(game, 42L);

        assertThat(result).contains("FINAL SCORES");
        assertThat(result).contains("Alice");
        assertThat(result).contains("Bob");
        assertThat(result).contains("Carol");
        assertThat(result).contains("Seed: 42");
    }

    @Test
    void should_show_winner_tag() {
        Game game = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);
        Player current = game.getCurrentPlayer();

        // Give current player enough VPs to win
        current.addSettlement();
        current.addSettlement();
        current.addSettlement();
        current.addSettlement();
        current.addSettlement();
        current.addSettlement(); // 7 settlements = 7 VP
        game.checkWinCondition();

        ScoreboardRenderer renderer = new ScoreboardRenderer();
        String result = renderer.render(game, 42L);

        assertThat(result).contains("WINNER");
        assertThat(result).contains("FINAL SCORES");
    }

    @Test
    void should_show_player_details() {
        Game game = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);
        Player alice = game.getPlayerByName("Alice");
        alice.setLongestRoute(true);
        alice.getHand().add(ResourceType.BRICK, 3);

        ScoreboardRenderer renderer = new ScoreboardRenderer();
        String result = renderer.render(game, 42L);

        assertThat(result).contains("Longest Route: YES");
        assertThat(result).contains("Largest Army: NO");
        assertThat(result).contains("Rounds played:");
    }

    @Test
    void should_sort_players_by_vp_descending() {
        Game game = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);
        Player carol = game.getPlayerByName("Carol");
        carol.addSettlement(); // Carol now has 2 VP

        ScoreboardRenderer renderer = new ScoreboardRenderer();
        String result = renderer.render(game, 42L);

        // Carol should appear before Alice and Bob
        int carolIndex = result.indexOf("Carol");
        int aliceIndex = result.indexOf("Alice");
        assertThat(carolIndex).isLessThan(aliceIndex);
    }

    @Test
    void should_render_no_winner_on_quit() {
        Game game = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);
        game.endGame(); // Quit without winner

        ScoreboardRenderer renderer = new ScoreboardRenderer();
        String result = renderer.render(game, 42L);

        assertThat(result).doesNotContain("WINNER");
    }
}
