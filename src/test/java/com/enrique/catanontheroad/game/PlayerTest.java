package com.enrique.catanontheroad.game;

import com.enrique.catanontheroad.game.card.Metropolis;
import com.enrique.catanontheroad.game.card.MetropolisSide;
import com.enrique.catanontheroad.game.card.MetropolisType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlayerTest {

    @Test
    void new_player_should_have_name_and_empty_state() {
        Player player = new Player("Alice");

        assertThat(player.getName()).isEqualTo("Alice");
        assertThat(player.getHand().isEmpty()).isTrue();
        assertThat(player.getRoadCount()).isZero();
        assertThat(player.getUncoveredSettlementCount()).isZero();
        assertThat(player.getUncoveredCityCount()).isZero();
        assertThat(player.getKnightCount()).isZero();
        assertThat(player.getMetropolisCount()).isZero();
        assertThat(player.hasLongestRoute()).isFalse();
        assertThat(player.hasLargestArmy()).isFalse();
    }

    @Test
    void add_road_should_increase_count() {
        Player player = new Player("Bob");

        player.addRoad();
        player.addRoad();

        assertThat(player.getRoadCount()).isEqualTo(2);
    }

    @Test
    void add_settlement_should_increase_count() {
        Player player = new Player("Carol");

        player.addSettlement();

        assertThat(player.getUncoveredSettlementCount()).isEqualTo(1);
        assertThat(player.hasUncoveredSettlement()).isTrue();
    }

    @Test
    void add_city_should_cover_settlement() {
        Player player = new Player("Alice");
        player.addSettlement();
        player.addSettlement();

        player.addCity();

        assertThat(player.getUncoveredSettlementCount()).isEqualTo(1);
        assertThat(player.getTotalSettlementCount()).isEqualTo(2);
        assertThat(player.getUncoveredCityCount()).isEqualTo(1);
    }

    @Test
    void add_city_without_settlement_should_throw() {
        Player player = new Player("Bob");

        assertThatThrownBy(player::addCity)
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("settlement");
    }

    @Test
    void add_knight_should_increase_count() {
        Player player = new Player("Carol");

        player.addKnight();
        player.addKnight();
        player.addKnight();

        assertThat(player.getKnightCount()).isEqualTo(3);
    }

    @Test
    void add_metropolis_should_cover_city() {
        Player player = new Player("Alice");
        player.addSettlement();
        player.addSettlement();
        player.addCity();
        player.addCity();

        Metropolis m = new Metropolis(MetropolisSide.A, MetropolisType.ROAD);
        player.addMetropolis(m);

        assertThat(player.getUncoveredCityCount()).isEqualTo(1);
        assertThat(player.getTotalCityCount()).isEqualTo(2);
        assertThat(player.getMetropolisCount()).isEqualTo(1);
        assertThat(player.getMetropolises()).containsExactly(m);
    }

    @Test
    void add_metropolis_without_city_should_throw() {
        Player player = new Player("Bob");

        Metropolis m = new Metropolis(MetropolisSide.A, MetropolisType.ROAD);

        assertThatThrownBy(() -> player.addMetropolis(m))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("city");
    }

    @Test
    void calculate_vp_should_sum_all_sources() {
        Player player = new Player("Alice");

        // 1 uncovered settlement = 1 VP
        player.addSettlement();

        // Build city (covers settlement) = 2 VP
        player.addSettlement();
        player.addCity();

        // Metropolis (covers city) = 3 VP
        player.addSettlement();
        player.addCity();
        player.addMetropolis(new Metropolis(MetropolisSide.A, MetropolisType.ROAD));

        // Bonuses
        player.setLongestRoute(true); // +2 VP
        player.setLargestArmy(true);  // +2 VP

        // Total: 1 + 2 + 3 + 2 + 2 = 10 VP
        assertThat(player.calculateVictoryPoints()).isEqualTo(10);
    }

    @Test
    void calculate_vp_with_only_settlements() {
        Player player = new Player("Bob");
        player.addSettlement();
        player.addSettlement();
        player.addSettlement();

        assertThat(player.calculateVictoryPoints()).isEqualTo(3);
    }

    @Test
    void harvest_bonus_for_cities_and_metropolises() {
        Player player = new Player("Alice");

        // Setup: 2 uncovered cities, 1 Metropolis A
        player.addSettlement();
        player.addSettlement();
        player.addSettlement();
        player.addCity();
        player.addCity();
        player.addCity();
        player.addMetropolis(new Metropolis(MetropolisSide.A, MetropolisType.ROAD));

        // 2 uncovered cities (+2) + 1 Metropolis A (+2) = 4
        assertThat(player.calculateHarvestBonus()).isEqualTo(4);
    }

    @Test
    void harvest_bonus_with_metropolis_b() {
        Player player = new Player("Bob");
        player.addSettlement();
        player.addCity();
        player.addMetropolis(new Metropolis(MetropolisSide.B, MetropolisType.KNIGHT));

        // 0 uncovered cities + 1 Metropolis B (+1) = 1
        assertThat(player.calculateHarvestBonus()).isEqualTo(1);
    }

    @Test
    void substitution_ratio_by_road_count() {
        Player player = new Player("Alice");

        assertThat(player.getSubstitutionRatio()).isEqualTo(4); // 0 roads

        player.addRoad();
        assertThat(player.getSubstitutionRatio()).isEqualTo(4); // 1 road

        player.addRoad();
        assertThat(player.getSubstitutionRatio()).isEqualTo(3); // 2 roads

        player.addRoad();
        assertThat(player.getSubstitutionRatio()).isEqualTo(2); // 3 roads

        player.addRoad();
        assertThat(player.getSubstitutionRatio()).isEqualTo(1); // 4 roads

        player.addRoad();
        assertThat(player.getSubstitutionRatio()).isEqualTo(1); // 5+ roads
    }

    @Test
    void robber_threshold_increases_with_knights() {
        Player player = new Player("Bob");

        assertThat(player.getRobberThreshold()).isEqualTo(7);

        player.addKnight();
        assertThat(player.getRobberThreshold()).isEqualTo(8);

        player.addKnight();
        player.addKnight();
        assertThat(player.getRobberThreshold()).isEqualTo(10);
    }

    @Test
    void bonus_setters_work_correctly() {
        Player player = new Player("Carol");

        player.setLongestRoute(true);
        assertThat(player.hasLongestRoute()).isTrue();

        player.setLargestArmy(true);
        assertThat(player.hasLargestArmy()).isTrue();

        player.setLongestRoute(false);
        assertThat(player.hasLongestRoute()).isFalse();
    }

    @Test
    void has_uncovered_city_should_return_correctly() {
        Player player = new Player("Alice");

        assertThat(player.hasUncoveredCity()).isFalse();

        player.addSettlement();
        player.addCity();
        assertThat(player.hasUncoveredCity()).isTrue();

        player.addMetropolis(new Metropolis(MetropolisSide.A, MetropolisType.ROAD));
        assertThat(player.hasUncoveredCity()).isFalse();
    }

    @Test
    void get_metropolises_should_return_copy() {
        Player player = new Player("Bob");
        player.addSettlement();
        player.addCity();
        player.addMetropolis(new Metropolis(MetropolisSide.A, MetropolisType.ROAD));

        var list = player.getMetropolises();
        list.clear(); // Modify the copy

        assertThat(player.getMetropolisCount()).isEqualTo(1); // Original unchanged
    }
}
