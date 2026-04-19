package com.enrique.catanontheroad.game.phase;

import com.enrique.catanontheroad.game.Game;
import com.enrique.catanontheroad.game.Player;
import com.enrique.catanontheroad.game.card.Metropolis;
import com.enrique.catanontheroad.game.card.MetropolisSide;
import com.enrique.catanontheroad.game.card.MetropolisType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HarvestPhaseTest {

    @Test
    void should_give_all_players_one_base_card() {
        Game game = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);
        HarvestPhase harvestPhase = new HarvestPhase();

        List<HarvestPhase.HarvestResult> results = harvestPhase.execute(game);

        assertThat(results).hasSize(3);
        for (HarvestPhase.HarvestResult result : results) {
            assertThat(result.cardsDrawn()).isNotEmpty();
        }
    }

    @Test
    void current_player_with_city_should_get_bonus_cards() {
        Game game = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);
        Player current = game.getCurrentPlayer();

        // Add city (which requires uncovered settlement first)
        current.addSettlement(); // Now has 2 settlements
        current.addCity();       // Covers one, now 1 settlement + 1 city

        int handBefore = current.getHand().total();
        HarvestPhase harvestPhase = new HarvestPhase();
        List<HarvestPhase.HarvestResult> results = harvestPhase.execute(game);

        // 1 base + 1 city bonus = 2 cards
        int handAfter = current.getHand().total();
        assertThat(handAfter - handBefore).isEqualTo(2);

        // First result should be current player with 2 cards drawn
        assertThat(results.get(0).player()).isEqualTo(current);
        assertThat(results.get(0).cardsDrawn()).hasSize(2);
    }

    @Test
    void current_player_with_metropolis_a_should_get_bonus_cards() {
        Game game = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);
        Player current = game.getCurrentPlayer();

        // Build up to metropolis
        current.addSettlement();
        current.addCity();
        current.addMetropolis(new Metropolis(MetropolisSide.A, MetropolisType.ROAD));

        int handBefore = current.getHand().total();
        HarvestPhase harvestPhase = new HarvestPhase();
        harvestPhase.execute(game);

        // 1 base + 2 metropolis A bonus = 3 cards
        int handAfter = current.getHand().total();
        assertThat(handAfter - handBefore).isEqualTo(3);
    }

    @Test
    void current_player_with_metropolis_b_should_get_smaller_bonus() {
        Game game = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.B, 42L);
        Player current = game.getCurrentPlayer();

        // Build up to metropolis
        current.addSettlement();
        current.addCity();
        current.addMetropolis(new Metropolis(MetropolisSide.B, MetropolisType.KNIGHT));

        int handBefore = current.getHand().total();
        HarvestPhase harvestPhase = new HarvestPhase();
        harvestPhase.execute(game);

        // 1 base + 1 metropolis B bonus = 2 cards
        int handAfter = current.getHand().total();
        assertThat(handAfter - handBefore).isEqualTo(2);
    }

    @Test
    void other_players_should_only_get_base_card() {
        Game game = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);
        Player current = game.getCurrentPlayer();

        // Give current player buildings
        current.addSettlement();
        current.addCity();

        // Get other players' hand sizes before
        List<Player> others = game.getOtherPlayers(current);
        int[] handsBefore = others.stream().mapToInt(p -> p.getHand().total()).toArray();

        HarvestPhase harvestPhase = new HarvestPhase();
        harvestPhase.execute(game);

        // Other players should each have 1 more card
        for (int i = 0; i < others.size(); i++) {
            assertThat(others.get(i).getHand().total()).isEqualTo(handsBefore[i] + 1);
        }
    }

    @Test
    void results_should_start_with_current_player() {
        Game game = new Game(List.of("Alice", "Bob", "Carol"), MetropolisSide.A, 42L);
        Player current = game.getCurrentPlayer();

        HarvestPhase harvestPhase = new HarvestPhase();
        List<HarvestPhase.HarvestResult> results = harvestPhase.execute(game);

        assertThat(results.get(0).player()).isEqualTo(current);
    }
}
