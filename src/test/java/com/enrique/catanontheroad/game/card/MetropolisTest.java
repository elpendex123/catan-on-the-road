package com.enrique.catanontheroad.game.card;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MetropolisTest {

    @Test
    void metropolis_should_have_correct_cost() {
        assertThat(Metropolis.COST).containsEntry(ResourceType.WOOL, 3);
        assertThat(Metropolis.COST).containsEntry(ResourceType.ORE, 1);
        assertThat(Metropolis.COST).hasSize(2);
    }

    @Test
    void metropolis_should_be_worth_three_vp() {
        assertThat(Metropolis.VICTORY_POINTS).isEqualTo(3);
    }

    @Test
    void metropolis_a_should_give_two_harvest_bonus() {
        Metropolis metropolisA = new Metropolis(MetropolisSide.A, MetropolisType.ROAD);
        assertThat(metropolisA.harvestBonus()).isEqualTo(2);
    }

    @Test
    void metropolis_b_should_give_one_harvest_bonus() {
        Metropolis metropolisB = new Metropolis(MetropolisSide.B, MetropolisType.ROAD);
        assertThat(metropolisB.harvestBonus()).isEqualTo(1);
    }

    @Test
    void only_b_longest_route_should_have_tie_break_for_longest_route() {
        Metropolis bLongestRoute = new Metropolis(MetropolisSide.B, MetropolisType.LONGEST_ROUTE);
        Metropolis bRoad = new Metropolis(MetropolisSide.B, MetropolisType.ROAD);
        Metropolis aLongestRoute = new Metropolis(MetropolisSide.A, MetropolisType.LONGEST_ROUTE);

        assertThat(bLongestRoute.hasTieBreakForLongestRoute()).isTrue();
        assertThat(bRoad.hasTieBreakForLongestRoute()).isFalse();
        assertThat(aLongestRoute.hasTieBreakForLongestRoute()).isFalse();
    }

    @Test
    void only_b_largest_army_should_have_tie_break_for_largest_army() {
        Metropolis bLargestArmy = new Metropolis(MetropolisSide.B, MetropolisType.LARGEST_ARMY);
        Metropolis bKnight = new Metropolis(MetropolisSide.B, MetropolisType.KNIGHT);
        Metropolis aLargestArmy = new Metropolis(MetropolisSide.A, MetropolisType.LARGEST_ARMY);

        assertThat(bLargestArmy.hasTieBreakForLargestArmy()).isTrue();
        assertThat(bKnight.hasTieBreakForLargestArmy()).isFalse();
        assertThat(aLargestArmy.hasTieBreakForLargestArmy()).isFalse();
    }

    @Test
    void only_b_road_should_have_on_build_draw_for_roads() {
        Metropolis bRoad = new Metropolis(MetropolisSide.B, MetropolisType.ROAD);
        Metropolis bKnight = new Metropolis(MetropolisSide.B, MetropolisType.KNIGHT);
        Metropolis aRoad = new Metropolis(MetropolisSide.A, MetropolisType.ROAD);

        assertThat(bRoad.hasOnBuildDrawForRoads()).isTrue();
        assertThat(bKnight.hasOnBuildDrawForRoads()).isFalse();
        assertThat(aRoad.hasOnBuildDrawForRoads()).isFalse();
    }

    @Test
    void only_b_knight_should_have_on_build_draw_for_knights() {
        Metropolis bKnight = new Metropolis(MetropolisSide.B, MetropolisType.KNIGHT);
        Metropolis bRoad = new Metropolis(MetropolisSide.B, MetropolisType.ROAD);
        Metropolis aKnight = new Metropolis(MetropolisSide.A, MetropolisType.KNIGHT);

        assertThat(bKnight.hasOnBuildDrawForKnights()).isTrue();
        assertThat(bRoad.hasOnBuildDrawForKnights()).isFalse();
        assertThat(aKnight.hasOnBuildDrawForKnights()).isFalse();
    }
}
