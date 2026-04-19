package com.enrique.catanontheroad.game;

import com.enrique.catanontheroad.game.card.MetropolisSide;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GameTest {

    @Test
    void should_initialize_with_three_players() {
        Game game = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);

        assertThat(game.getPlayers()).hasSize(3);
        assertThat(game.getPlayers().get(0).getName()).isEqualTo("Alice");
        assertThat(game.getPlayers().get(1).getName()).isEqualTo("Bob");
        assertThat(game.getPlayers().get(2).getName()).isEqualTo("Carol");
    }

    @Test
    void should_reject_non_three_players() {
        assertThatThrownBy(() -> new Game(List.of("Alice", "Bob"), MetropolisSide.A, 42L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("3 players");
    }

    @Test
    void each_player_should_start_with_settlement_and_road() {
        Game game = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);

        for (Player player : game.getPlayers()) {
            assertThat(player.getUncoveredSettlementCount()).isEqualTo(1);
            assertThat(player.getRoadCount()).isEqualTo(1);
        }
    }

    @Test
    void each_player_should_start_with_two_resource_cards() {
        Game game = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);

        for (Player player : game.getPlayers()) {
            assertThat(player.getHand().total()).isEqualTo(2);
        }
    }

    @Test
    void should_start_at_round_1() {
        Game game = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);

        assertThat(game.getRound()).isEqualTo(1);
    }

    @Test
    void advance_to_next_player_should_cycle_through_players() {
        Game game = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);
        int startIndex = game.getCurrentPlayerIndex();

        game.advanceToNextPlayer();
        assertThat(game.getCurrentPlayerIndex()).isEqualTo((startIndex + 1) % 3);

        game.advanceToNextPlayer();
        assertThat(game.getCurrentPlayerIndex()).isEqualTo((startIndex + 2) % 3);
    }

    @Test
    void round_should_increment_after_full_cycle() {
        Game game = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);
        // Ensure we're starting at index 0 for predictable testing
        while (game.getCurrentPlayerIndex() != 0) {
            game.advanceToNextPlayer();
        }
        int initialRound = game.getRound();

        game.advanceToNextPlayer(); // index 1
        game.advanceToNextPlayer(); // index 2
        assertThat(game.getRound()).isEqualTo(initialRound);

        game.advanceToNextPlayer(); // index 0 -> new round
        assertThat(game.getRound()).isEqualTo(initialRound + 1);
    }

    @Test
    void should_have_correct_metropolis_side() {
        Game gameA = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);
        Game gameB = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.B, 42L);

        assertThat(gameA.getMetropolisSide()).isEqualTo(MetropolisSide.A);
        assertThat(gameB.getMetropolisSide()).isEqualTo(MetropolisSide.B);
    }

    @Test
    void should_not_be_ended_initially() {
        Game game = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);

        assertThat(game.isGameEnded()).isFalse();
        assertThat(game.getWinner()).isNull();
    }

    @Test
    void check_win_condition_should_detect_7_vp() {
        Game game = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);
        Player current = game.getCurrentPlayer();

        // Starting VP = 1 (starting settlement)
        // Need 6 more VPs to reach 7
        // Add 3 more settlements = 4 VPs total
        current.addSettlement();
        current.addSettlement();
        current.addSettlement();
        // Now 4 settlements = 4 VP, need 3 more
        // Add a city (covers settlement, adds 2-1=1 net VP) = 5 VP
        current.addCity();
        // Add longest route = 7 VP
        current.setLongestRoute(true);

        assertThat(current.calculateVictoryPoints()).isEqualTo(7);
        assertThat(game.checkWinCondition()).isTrue();
        assertThat(game.isGameEnded()).isTrue();
        assertThat(game.getWinner()).isEqualTo(current);
    }

    @Test
    void get_player_by_name_should_find_player() {
        Game game = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);

        Player bob = game.getPlayerByName("Bob");

        assertThat(bob.getName()).isEqualTo("Bob");
    }

    @Test
    void get_player_by_name_should_throw_for_unknown_name() {
        Game game = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);

        assertThatThrownBy(() -> game.getPlayerByName("Dave"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("not found");
    }

    @Test
    void get_other_players_should_exclude_current() {
        Game game = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);
        Player current = game.getCurrentPlayer();

        List<Player> others = game.getOtherPlayers(current);

        assertThat(others).hasSize(2);
        assertThat(others).doesNotContain(current);
    }

    @Test
    void end_game_should_set_game_ended_flag() {
        Game game = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);

        game.endGame();

        assertThat(game.isGameEnded()).isTrue();
    }

    @Test
    void should_be_deterministic_with_same_seed() {
        Game game1 = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 12345L);
        Game game2 = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 12345L);

        assertThat(game1.getCurrentPlayerIndex()).isEqualTo(game2.getCurrentPlayerIndex());

        // Check starting hands match
        for (int i = 0; i < 3; i++) {
            Player p1 = game1.getPlayers().get(i);
            Player p2 = game2.getPlayers().get(i);
            assertThat(p1.getHand().getCards()).isEqualTo(p2.getHand().getCards());
        }
    }

    @Test
    void update_bonuses_should_check_longest_route_and_largest_army() {
        Game game = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);
        Player alice = game.getPlayers().get(0);

        // Add roads to qualify for longest route
        alice.addRoad();
        alice.addRoad(); // Now 3 roads

        game.updateBonuses();

        assertThat(game.getLongestRoute().getHolder()).isEqualTo(alice);
        assertThat(alice.hasLongestRoute()).isTrue();
    }
}
