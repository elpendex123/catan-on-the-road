package com.enrique.catanontheroad.service;

import com.enrique.catanontheroad.game.card.MetropolisSide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GameServiceTest {

    private GameService service;

    @BeforeEach
    void setUp() {
        service = new GameService();
    }

    @Test
    void should_not_have_active_game_initially() {
        assertThat(service.hasActiveGame()).isFalse();
    }

    @Test
    void should_create_game() {
        service.createGame(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);

        assertThat(service.hasActiveGame()).isTrue();
        assertThat(service.getGame()).isNotNull();
        assertThat(service.getEngine()).isNotNull();
        assertThat(service.getBuildAction()).isNotNull();
        assertThat(service.getTradeAction()).isNotNull();
        assertThat(service.getSubstituteAction()).isNotNull();
        assertThat(service.getEventResolver()).isNotNull();
        assertThat(service.getSeed()).isEqualTo(42L);
    }

    @Test
    void should_return_current_player() {
        service.createGame(List.of("Alice", "Bob", "Carol"), MetropolisSide.B, 10L);

        assertThat(service.getCurrentPlayer()).isNotNull();
        assertThat(service.getCurrentPlayer().getName()).isIn("Alice", "Bob", "Carol");
    }

    @Test
    void should_return_all_players() {
        service.createGame(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);

        assertThat(service.getPlayers()).hasSize(3);
    }

    @Test
    void should_end_game() {
        service.createGame(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);

        service.endGame();

        assertThat(service.getGame().isGameEnded()).isTrue();
    }

    @Test
    void should_clear_game() {
        service.createGame(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);

        service.clearGame();

        assertThat(service.hasActiveGame()).isFalse();
    }

    @Test
    void should_handle_end_game_when_no_game() {
        service.endGame(); // Should not throw
        assertThat(service.hasActiveGame()).isFalse();
    }
}
