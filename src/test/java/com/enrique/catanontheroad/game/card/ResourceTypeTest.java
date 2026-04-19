package com.enrique.catanontheroad.game.card;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceTypeTest {

    @Test
    void should_have_five_resource_types() {
        assertThat(ResourceType.values()).hasSize(5);
    }

    @Test
    void should_contain_all_expected_types() {
        assertThat(ResourceType.values()).containsExactly(
            ResourceType.BRICK,
            ResourceType.WOOD,
            ResourceType.WOOL,
            ResourceType.WHEAT,
            ResourceType.ORE
        );
    }
}
