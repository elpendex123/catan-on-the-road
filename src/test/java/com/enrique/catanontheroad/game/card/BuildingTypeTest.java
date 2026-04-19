package com.enrique.catanontheroad.game.card;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BuildingTypeTest {

    @Test
    void should_have_four_building_types() {
        assertThat(BuildingType.values()).hasSize(4);
    }

    @Test
    void should_contain_all_expected_types() {
        assertThat(BuildingType.values()).containsExactly(
            BuildingType.ROAD,
            BuildingType.SETTLEMENT,
            BuildingType.CITY,
            BuildingType.KNIGHT
        );
    }
}
