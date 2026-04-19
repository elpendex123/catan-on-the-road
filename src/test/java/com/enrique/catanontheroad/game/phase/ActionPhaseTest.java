package com.enrique.catanontheroad.game.phase;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ActionPhaseTest {

    @Test
    void should_have_trade_and_substitute_available_initially() {
        ActionPhase phase = new ActionPhase();

        assertThat(phase.isTradeAvailable()).isTrue();
        assertThat(phase.isSubstituteAvailable()).isTrue();
    }

    @Test
    void mark_trade_used_should_make_trade_unavailable() {
        ActionPhase phase = new ActionPhase();

        phase.markTradeUsed();

        assertThat(phase.isTradeAvailable()).isFalse();
        assertThat(phase.isSubstituteAvailable()).isTrue();
    }

    @Test
    void mark_substitute_used_should_make_substitute_unavailable() {
        ActionPhase phase = new ActionPhase();

        phase.markSubstituteUsed();

        assertThat(phase.isTradeAvailable()).isTrue();
        assertThat(phase.isSubstituteAvailable()).isFalse();
    }

    @Test
    void both_can_be_marked_used() {
        ActionPhase phase = new ActionPhase();

        phase.markTradeUsed();
        phase.markSubstituteUsed();

        assertThat(phase.isTradeAvailable()).isFalse();
        assertThat(phase.isSubstituteAvailable()).isFalse();
    }

    @Test
    void reset_should_restore_availability() {
        ActionPhase phase = new ActionPhase();
        phase.markTradeUsed();
        phase.markSubstituteUsed();

        phase.reset();

        assertThat(phase.isTradeAvailable()).isTrue();
        assertThat(phase.isSubstituteAvailable()).isTrue();
    }
}
