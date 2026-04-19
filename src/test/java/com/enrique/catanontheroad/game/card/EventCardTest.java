package com.enrique.catanontheroad.game.card;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EventCardTest {

    @Test
    void robber_should_have_correct_name() {
        EventCard.Robber robber = new EventCard.Robber();
        assertThat(robber.name()).isEqualTo("Robber");
    }

    @Test
    void abundance_should_have_correct_name() {
        EventCard.Abundance abundance = new EventCard.Abundance();
        assertThat(abundance.name()).isEqualTo("Abundance");
    }

    @Test
    void charity_should_have_correct_name() {
        EventCard.Charity charity = new EventCard.Charity();
        assertThat(charity.name()).isEqualTo("Charity");
    }

    @Test
    void solstice_should_have_correct_name() {
        EventCard.Solstice solstice = new EventCard.Solstice();
        assertThat(solstice.name()).isEqualTo("Solstice");
    }

    @Test
    void subsidy_should_have_correct_name() {
        EventCard.Subsidy subsidy = new EventCard.Subsidy();
        assertThat(subsidy.name()).isEqualTo("Subsidy");
    }
}
