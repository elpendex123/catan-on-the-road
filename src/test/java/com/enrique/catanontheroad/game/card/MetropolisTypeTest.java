package com.enrique.catanontheroad.game.card;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MetropolisTypeTest {

    @Test
    void should_have_four_metropolis_types() {
        assertThat(MetropolisType.values()).hasSize(4);
    }

    @Test
    void should_contain_all_expected_types() {
        assertThat(MetropolisType.values()).containsExactly(
            MetropolisType.ROAD,
            MetropolisType.LONGEST_ROUTE,
            MetropolisType.KNIGHT,
            MetropolisType.LARGEST_ARMY
        );
    }
}
