package com.enrique.catanontheroad.game.card;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BuildingCardTest {

    @Test
    void road_should_have_correct_cost_and_zero_vp() {
        BuildingCard.Road road = new BuildingCard.Road();

        assertThat(road.type()).isEqualTo(BuildingType.ROAD);
        assertThat(road.cost()).containsEntry(ResourceType.BRICK, 1);
        assertThat(road.cost()).containsEntry(ResourceType.WOOD, 1);
        assertThat(road.cost()).hasSize(2);
        assertThat(road.victoryPoints()).isZero();
    }

    @Test
    void settlement_should_have_correct_cost_and_one_vp() {
        BuildingCard.Settlement settlement = new BuildingCard.Settlement();

        assertThat(settlement.type()).isEqualTo(BuildingType.SETTLEMENT);
        assertThat(settlement.cost()).containsEntry(ResourceType.BRICK, 1);
        assertThat(settlement.cost()).containsEntry(ResourceType.WOOD, 1);
        assertThat(settlement.cost()).containsEntry(ResourceType.WOOL, 1);
        assertThat(settlement.cost()).containsEntry(ResourceType.WHEAT, 1);
        assertThat(settlement.cost()).hasSize(4);
        assertThat(settlement.victoryPoints()).isEqualTo(1);
    }

    @Test
    void city_should_have_correct_cost_and_two_vp() {
        BuildingCard.City city = new BuildingCard.City();

        assertThat(city.type()).isEqualTo(BuildingType.CITY);
        assertThat(city.cost()).containsEntry(ResourceType.WHEAT, 2);
        assertThat(city.cost()).containsEntry(ResourceType.ORE, 3);
        assertThat(city.cost()).hasSize(2);
        assertThat(city.victoryPoints()).isEqualTo(2);
    }

    @Test
    void knight_should_have_correct_cost_and_zero_vp() {
        BuildingCard.Knight knight = new BuildingCard.Knight();

        assertThat(knight.type()).isEqualTo(BuildingType.KNIGHT);
        assertThat(knight.cost()).containsEntry(ResourceType.WOOL, 1);
        assertThat(knight.cost()).containsEntry(ResourceType.ORE, 1);
        assertThat(knight.cost()).hasSize(2);
        assertThat(knight.victoryPoints()).isZero();
    }
}
