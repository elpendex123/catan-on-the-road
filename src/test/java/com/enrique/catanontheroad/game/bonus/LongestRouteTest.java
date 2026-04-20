package com.enrique.catanontheroad.game.bonus;

import com.enrique.catanontheroad.game.Player;
import com.enrique.catanontheroad.game.card.Metropolis;
import com.enrique.catanontheroad.game.card.MetropolisSide;
import com.enrique.catanontheroad.game.card.MetropolisType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LongestRouteTest {

    private Player alice;
    private Player bob;
    private Player carol;
    private List<Player> players;

    @BeforeEach
    void setUp() {
        alice = new Player("Alice");
        bob = new Player("Bob");
        carol = new Player("Carol");
        players = List.of(alice, bob, carol);
    }

    @Test
    void should_have_no_holder_initially() {
        LongestRoute lr = new LongestRoute();

        assertThat(lr.hasHolder()).isFalse();
        assertThat(lr.getHolder()).isNull();
        assertThat(lr.getHolderRoadCount()).isZero();
    }

    @Test
    void first_player_to_reach_3_roads_gets_bonus() {
        LongestRoute lr = new LongestRoute();

        alice.addRoad();
        alice.addRoad();
        lr.checkAndUpdate(players, MetropolisSide.A);
        assertThat(lr.hasHolder()).isFalse();

        alice.addRoad();
        lr.checkAndUpdate(players, MetropolisSide.A);

        assertThat(lr.hasHolder()).isTrue();
        assertThat(lr.getHolder()).isEqualTo(alice);
        assertThat(alice.hasLongestRoute()).isTrue();
    }

    @Test
    void bonus_transfers_on_strictly_more_roads() {
        LongestRoute lr = new LongestRoute();

        // Alice gets 3 roads first
        alice.addRoad();
        alice.addRoad();
        alice.addRoad();
        lr.checkAndUpdate(players, MetropolisSide.A);
        assertThat(lr.getHolder()).isEqualTo(alice);

        // Bob ties with 3 roads - no transfer
        bob.addRoad();
        bob.addRoad();
        bob.addRoad();
        lr.checkAndUpdate(players, MetropolisSide.A);
        assertThat(lr.getHolder()).isEqualTo(alice);

        // Bob gets 4 roads - transfer
        bob.addRoad();
        lr.checkAndUpdate(players, MetropolisSide.A);
        assertThat(lr.getHolder()).isEqualTo(bob);
        assertThat(alice.hasLongestRoute()).isFalse();
        assertThat(bob.hasLongestRoute()).isTrue();
    }

    @Test
    void a_side_holder_keeps_on_tie() {
        LongestRoute lr = new LongestRoute();

        alice.addRoad();
        alice.addRoad();
        alice.addRoad();
        lr.checkAndUpdate(players, MetropolisSide.A);

        bob.addRoad();
        bob.addRoad();
        bob.addRoad();
        lr.checkAndUpdate(players, MetropolisSide.A);

        // Alice still holds (current holder keeps on tie)
        assertThat(lr.getHolder()).isEqualTo(alice);
    }

    @Test
    void b_side_metropolis_holder_wins_ties() {
        LongestRoute lr = new LongestRoute();

        // Alice gets longest route first
        alice.addRoad();
        alice.addRoad();
        alice.addRoad();
        lr.checkAndUpdate(players, MetropolisSide.B);
        assertThat(lr.getHolder()).isEqualTo(alice);

        // Bob ties with 3 roads
        bob.addRoad();
        bob.addRoad();
        bob.addRoad();

        // Bob also has the B-Longest-Route metropolis
        bob.addSettlement();
        bob.addCity();
        bob.addMetropolis(new Metropolis(MetropolisSide.B, MetropolisType.LONGEST_ROUTE));

        lr.checkAndUpdate(players, MetropolisSide.B);

        // Bob wins the tie because of metropolis
        assertThat(lr.getHolder()).isEqualTo(bob);
    }

    @Test
    void holder_road_count_returns_correct_value() {
        LongestRoute lr = new LongestRoute();

        alice.addRoad();
        alice.addRoad();
        alice.addRoad();
        alice.addRoad();
        lr.checkAndUpdate(players, MetropolisSide.A);

        assertThat(lr.getHolderRoadCount()).isEqualTo(4);
    }

    @Test
    void b_side_tiebreak_holder_keeps_bonus_when_already_holder() {
        LongestRoute lr = new LongestRoute();

        // Alice gets longest route first and has the metropolis
        alice.addRoad();
        alice.addRoad();
        alice.addRoad();
        alice.addSettlement();
        alice.addCity();
        alice.addMetropolis(new Metropolis(MetropolisSide.B, MetropolisType.LONGEST_ROUTE));
        lr.checkAndUpdate(players, MetropolisSide.B);
        assertThat(lr.getHolder()).isEqualTo(alice);

        // Bob ties at 3 roads
        bob.addRoad();
        bob.addRoad();
        bob.addRoad();
        lr.checkAndUpdate(players, MetropolisSide.B);

        // Alice keeps it (she's already holder AND has tiebreak)
        assertThat(lr.getHolder()).isEqualTo(alice);
    }

    @Test
    void b_side_tie_without_metropolis_holder_keeps_current() {
        LongestRoute lr = new LongestRoute();

        // Alice gets longest route
        alice.addRoad();
        alice.addRoad();
        alice.addRoad();
        lr.checkAndUpdate(players, MetropolisSide.B);

        // Bob ties but nobody has the tiebreak metropolis
        bob.addRoad();
        bob.addRoad();
        bob.addRoad();
        lr.checkAndUpdate(players, MetropolisSide.B);

        // Alice keeps it (no tiebreak holder, current holder retains)
        assertThat(lr.getHolder()).isEqualTo(alice);
    }

    @Test
    void metropolis_holder_does_not_win_without_tie() {
        LongestRoute lr = new LongestRoute();

        // Alice gets 4 roads
        alice.addRoad();
        alice.addRoad();
        alice.addRoad();
        alice.addRoad();
        lr.checkAndUpdate(players, MetropolisSide.B);

        // Bob has only 3 roads but has the metropolis
        bob.addRoad();
        bob.addRoad();
        bob.addRoad();
        bob.addSettlement();
        bob.addCity();
        bob.addMetropolis(new Metropolis(MetropolisSide.B, MetropolisType.LONGEST_ROUTE));

        lr.checkAndUpdate(players, MetropolisSide.B);

        // Alice still holds (strictly more roads)
        assertThat(lr.getHolder()).isEqualTo(alice);
    }

    @Test
    void third_player_tie_should_not_steal_from_tiebreak_holder() {
        LongestRoute lr = new LongestRoute();

        // Bob gets the metropolis and longest route
        bob.addRoad();
        bob.addRoad();
        bob.addRoad();
        bob.addSettlement();
        bob.addCity();
        bob.addMetropolis(new Metropolis(MetropolisSide.B, MetropolisType.LONGEST_ROUTE));
        lr.checkAndUpdate(players, MetropolisSide.B);
        assertThat(lr.getHolder()).isEqualTo(bob);

        // Carol ties with Bob at 3 roads but is NOT the tiebreak holder
        carol.addRoad();
        carol.addRoad();
        carol.addRoad();
        lr.checkAndUpdate(players, MetropolisSide.B);

        // Bob keeps it (he's holder AND tiebreak holder)
        assertThat(lr.getHolder()).isEqualTo(bob);
    }

    @Test
    void player_with_non_tiebreak_metropolis_does_not_win_tie() {
        LongestRoute lr = new LongestRoute();

        // Alice gets 3 roads and a non-tiebreak metropolis
        alice.addRoad();
        alice.addRoad();
        alice.addRoad();
        alice.addSettlement();
        alice.addCity();
        alice.addMetropolis(new Metropolis(MetropolisSide.B, MetropolisType.ROAD));
        lr.checkAndUpdate(players, MetropolisSide.B);
        assertThat(lr.getHolder()).isEqualTo(alice);

        // Bob ties at 3 roads with the actual tiebreak metropolis
        bob.addRoad();
        bob.addRoad();
        bob.addRoad();
        bob.addSettlement();
        bob.addCity();
        bob.addMetropolis(new Metropolis(MetropolisSide.B, MetropolisType.LONGEST_ROUTE));
        lr.checkAndUpdate(players, MetropolisSide.B);

        // Bob wins the tie because he has the tiebreak metropolis
        assertThat(lr.getHolder()).isEqualTo(bob);
    }
}
