package com.enrique.catanontheroad.game.deck;

import com.enrique.catanontheroad.game.card.Metropolis;
import com.enrique.catanontheroad.game.card.MetropolisSide;
import com.enrique.catanontheroad.game.card.MetropolisType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MetropolisStackTest {

    @Test
    void should_have_4_metropolises_at_start() {
        MetropolisStack stack = new MetropolisStack(MetropolisSide.A);

        assertThat(stack.remainingCount()).isEqualTo(4);
        assertThat(stack.isEmpty()).isFalse();
    }

    @Test
    void should_have_correct_side() {
        MetropolisStack stackA = new MetropolisStack(MetropolisSide.A);
        MetropolisStack stackB = new MetropolisStack(MetropolisSide.B);

        assertThat(stackA.getSide()).isEqualTo(MetropolisSide.A);
        assertThat(stackB.getSide()).isEqualTo(MetropolisSide.B);
    }

    @Test
    void should_contain_all_four_types() {
        MetropolisStack stack = new MetropolisStack(MetropolisSide.A);

        assertThat(stack.isAvailable(MetropolisType.ROAD)).isTrue();
        assertThat(stack.isAvailable(MetropolisType.LONGEST_ROUTE)).isTrue();
        assertThat(stack.isAvailable(MetropolisType.KNIGHT)).isTrue();
        assertThat(stack.isAvailable(MetropolisType.LARGEST_ARMY)).isTrue();
    }

    @Test
    void take_by_type_should_remove_and_return_metropolis() {
        MetropolisStack stack = new MetropolisStack(MetropolisSide.B);

        Metropolis taken = stack.take(MetropolisType.ROAD);

        assertThat(taken.type()).isEqualTo(MetropolisType.ROAD);
        assertThat(taken.side()).isEqualTo(MetropolisSide.B);
        assertThat(stack.remainingCount()).isEqualTo(3);
        assertThat(stack.isAvailable(MetropolisType.ROAD)).isFalse();
    }

    @Test
    void take_by_index_should_remove_and_return_metropolis() {
        MetropolisStack stack = new MetropolisStack(MetropolisSide.A);

        Metropolis taken = stack.take(0);

        assertThat(taken).isNotNull();
        assertThat(stack.remainingCount()).isEqualTo(3);
    }

    @Test
    void take_unavailable_type_should_throw() {
        MetropolisStack stack = new MetropolisStack(MetropolisSide.A);
        stack.take(MetropolisType.ROAD);

        assertThatThrownBy(() -> stack.take(MetropolisType.ROAD))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("not available");
    }

    @Test
    void take_invalid_index_should_throw() {
        MetropolisStack stack = new MetropolisStack(MetropolisSide.A);

        assertThatThrownBy(() -> stack.take(-1))
            .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> stack.take(4))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void get_available_should_return_copy() {
        MetropolisStack stack = new MetropolisStack(MetropolisSide.A);

        List<Metropolis> available = stack.getAvailable();
        available.clear(); // Modifying the returned list

        assertThat(stack.remainingCount()).isEqualTo(4); // Original unchanged
    }

    @Test
    void should_be_empty_after_taking_all() {
        MetropolisStack stack = new MetropolisStack(MetropolisSide.A);

        stack.take(MetropolisType.ROAD);
        stack.take(MetropolisType.LONGEST_ROUTE);
        stack.take(MetropolisType.KNIGHT);
        stack.take(MetropolisType.LARGEST_ARMY);

        assertThat(stack.isEmpty()).isTrue();
        assertThat(stack.remainingCount()).isZero();
    }

    @Test
    void metropolis_a_should_have_harvest_bonus_2() {
        MetropolisStack stack = new MetropolisStack(MetropolisSide.A);
        Metropolis m = stack.take(MetropolisType.ROAD);

        assertThat(m.harvestBonus()).isEqualTo(2);
    }

    @Test
    void metropolis_b_should_have_harvest_bonus_1() {
        MetropolisStack stack = new MetropolisStack(MetropolisSide.B);
        Metropolis m = stack.take(MetropolisType.ROAD);

        assertThat(m.harvestBonus()).isEqualTo(1);
    }
}
