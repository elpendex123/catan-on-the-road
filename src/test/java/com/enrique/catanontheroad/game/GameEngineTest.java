package com.enrique.catanontheroad.game;

import com.enrique.catanontheroad.game.card.MetropolisSide;
import com.enrique.catanontheroad.game.card.ResourceType;
import com.enrique.catanontheroad.game.phase.HarvestPhase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GameEngineTest {

    private Game game;
    private GameEngine engine;

    @BeforeEach
    void setUp() {
        game = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);
        engine = new GameEngine(game);
    }

    @Test
    void should_return_game() {
        assertThat(engine.getGame()).isSameAs(game);
    }

    @Test
    void execute_harvest_phase_should_give_all_players_one_card() {
        int aliceHandBefore = game.getPlayers().get(0).getHand().total();
        int bobHandBefore = game.getPlayers().get(1).getHand().total();
        int carolHandBefore = game.getPlayers().get(2).getHand().total();

        List<HarvestPhase.HarvestResult> results = engine.executeHarvestPhase();

        assertThat(results).hasSize(3);
        assertThat(game.getPlayers().get(0).getHand().total()).isEqualTo(aliceHandBefore + 1);
        assertThat(game.getPlayers().get(1).getHand().total()).isEqualTo(bobHandBefore + 1);
        assertThat(game.getPlayers().get(2).getHand().total()).isEqualTo(carolHandBefore + 1);
    }

    @Test
    void execute_harvest_phase_should_create_action_phase() {
        assertThat(engine.getCurrentActionPhase()).isNull();

        engine.executeHarvestPhase();

        assertThat(engine.getCurrentActionPhase()).isNotNull();
    }

    @Test
    void trade_and_substitute_should_be_available_after_harvest() {
        engine.executeHarvestPhase();

        assertThat(engine.isTradeAvailable()).isTrue();
        assertThat(engine.isSubstituteAvailable()).isTrue();
    }

    @Test
    void mark_trade_used_should_make_trade_unavailable() {
        engine.executeHarvestPhase();

        engine.markTradeUsed();

        assertThat(engine.isTradeAvailable()).isFalse();
        assertThat(engine.isSubstituteAvailable()).isTrue();
    }

    @Test
    void mark_substitute_used_should_make_substitute_unavailable() {
        engine.executeHarvestPhase();

        engine.markSubstituteUsed();

        assertThat(engine.isTradeAvailable()).isTrue();
        assertThat(engine.isSubstituteAvailable()).isFalse();
    }

    @Test
    void end_turn_should_advance_player() {
        Player firstPlayer = engine.getCurrentPlayer();
        engine.executeHarvestPhase();

        engine.endTurn();

        assertThat(engine.getCurrentPlayer()).isNotEqualTo(firstPlayer);
        assertThat(engine.getCurrentActionPhase()).isNull();
    }

    @Test
    void draw_resources_should_add_to_player_hand() {
        Player player = engine.getCurrentPlayer();
        int handBefore = player.getHand().total();

        List<ResourceType> drawn = engine.drawResources(player, 3);

        assertThat(drawn).hasSize(3);
        assertThat(player.getHand().total()).isEqualTo(handBefore + 3);
    }

    @Test
    void discard_resources_should_remove_from_player_hand() {
        Player player = engine.getCurrentPlayer();
        // First draw some cards to ensure we have something to discard
        engine.drawResources(player, 5);

        // Find a resource type the player has
        ResourceType typeToDiscard = null;
        for (ResourceType type : ResourceType.values()) {
            if (player.getHand().has(type)) {
                typeToDiscard = type;
                break;
            }
        }

        int countBefore = player.getHand().count(typeToDiscard);

        engine.discardResources(player, typeToDiscard, 1);

        assertThat(player.getHand().count(typeToDiscard)).isEqualTo(countBefore - 1);
    }

    @Test
    void update_bonuses_should_delegate_to_game() {
        Player player = game.getPlayers().get(0);
        player.addRoad();
        player.addRoad(); // Now 3 roads

        engine.updateBonuses();

        assertThat(player.hasLongestRoute()).isTrue();
    }

    @Test
    void get_players_should_return_all_players() {
        assertThat(engine.getPlayers()).hasSize(3);
    }
}
