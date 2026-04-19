package com.enrique.catanontheroad.game.bonus;

import com.enrique.catanontheroad.game.Player;
import com.enrique.catanontheroad.game.card.Metropolis;
import com.enrique.catanontheroad.game.card.MetropolisSide;
import com.enrique.catanontheroad.game.card.MetropolisType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LargestArmyTest {

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
        LargestArmy la = new LargestArmy();

        assertThat(la.hasHolder()).isFalse();
        assertThat(la.getHolder()).isNull();
        assertThat(la.getHolderKnightCount()).isZero();
    }

    @Test
    void first_player_to_reach_2_knights_gets_bonus() {
        LargestArmy la = new LargestArmy();

        alice.addKnight();
        la.checkAndUpdate(players, MetropolisSide.A);
        assertThat(la.hasHolder()).isFalse();

        alice.addKnight();
        la.checkAndUpdate(players, MetropolisSide.A);

        assertThat(la.hasHolder()).isTrue();
        assertThat(la.getHolder()).isEqualTo(alice);
        assertThat(alice.hasLargestArmy()).isTrue();
    }

    @Test
    void bonus_transfers_on_strictly_more_knights() {
        LargestArmy la = new LargestArmy();

        // Alice gets 2 knights first
        alice.addKnight();
        alice.addKnight();
        la.checkAndUpdate(players, MetropolisSide.A);
        assertThat(la.getHolder()).isEqualTo(alice);

        // Bob ties with 2 knights - no transfer
        bob.addKnight();
        bob.addKnight();
        la.checkAndUpdate(players, MetropolisSide.A);
        assertThat(la.getHolder()).isEqualTo(alice);

        // Bob gets 3 knights - transfer
        bob.addKnight();
        la.checkAndUpdate(players, MetropolisSide.A);
        assertThat(la.getHolder()).isEqualTo(bob);
        assertThat(alice.hasLargestArmy()).isFalse();
        assertThat(bob.hasLargestArmy()).isTrue();
    }

    @Test
    void a_side_holder_keeps_on_tie() {
        LargestArmy la = new LargestArmy();

        alice.addKnight();
        alice.addKnight();
        la.checkAndUpdate(players, MetropolisSide.A);

        bob.addKnight();
        bob.addKnight();
        la.checkAndUpdate(players, MetropolisSide.A);

        // Alice still holds (current holder keeps on tie)
        assertThat(la.getHolder()).isEqualTo(alice);
    }

    @Test
    void b_side_metropolis_holder_wins_ties() {
        LargestArmy la = new LargestArmy();

        // Alice gets largest army first
        alice.addKnight();
        alice.addKnight();
        la.checkAndUpdate(players, MetropolisSide.B);
        assertThat(la.getHolder()).isEqualTo(alice);

        // Bob ties with 2 knights
        bob.addKnight();
        bob.addKnight();

        // Bob also has the B-Largest-Army metropolis
        bob.addSettlement();
        bob.addCity();
        bob.addMetropolis(new Metropolis(MetropolisSide.B, MetropolisType.LARGEST_ARMY));

        la.checkAndUpdate(players, MetropolisSide.B);

        // Bob wins the tie because of metropolis
        assertThat(la.getHolder()).isEqualTo(bob);
    }

    @Test
    void holder_knight_count_returns_correct_value() {
        LargestArmy la = new LargestArmy();

        alice.addKnight();
        alice.addKnight();
        alice.addKnight();
        la.checkAndUpdate(players, MetropolisSide.A);

        assertThat(la.getHolderKnightCount()).isEqualTo(3);
    }

    @Test
    void metropolis_holder_does_not_win_without_tie() {
        LargestArmy la = new LargestArmy();

        // Alice gets 3 knights
        alice.addKnight();
        alice.addKnight();
        alice.addKnight();
        la.checkAndUpdate(players, MetropolisSide.B);

        // Bob has only 2 knights but has the metropolis
        bob.addKnight();
        bob.addKnight();
        bob.addSettlement();
        bob.addCity();
        bob.addMetropolis(new Metropolis(MetropolisSide.B, MetropolisType.LARGEST_ARMY));

        la.checkAndUpdate(players, MetropolisSide.B);

        // Alice still holds (strictly more knights)
        assertThat(la.getHolder()).isEqualTo(alice);
    }
}
