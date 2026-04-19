package com.enrique.catanontheroad.game.card;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MetropolisSideTest {

    @Test
    void should_have_two_sides() {
        assertThat(MetropolisSide.values()).hasSize(2);
    }

    @Test
    void should_contain_a_and_b() {
        assertThat(MetropolisSide.values()).containsExactly(
            MetropolisSide.A,
            MetropolisSide.B
        );
    }
}
