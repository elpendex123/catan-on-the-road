package com.enrique.catanontheroad.game.rng;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SeededRandomTest {

    @Test
    void should_return_seed() {
        SeededRandom rng = new SeededRandom(42L);
        assertThat(rng.getSeed()).isEqualTo(42L);
    }

    @Test
    void should_produce_deterministic_sequence_with_same_seed() {
        SeededRandom rng1 = new SeededRandom(12345L);
        SeededRandom rng2 = new SeededRandom(12345L);

        for (int i = 0; i < 100; i++) {
            assertThat(rng1.nextInt(1000)).isEqualTo(rng2.nextInt(1000));
        }
    }

    @Test
    void should_produce_different_sequences_with_different_seeds() {
        SeededRandom rng1 = new SeededRandom(1L);
        SeededRandom rng2 = new SeededRandom(2L);

        List<Integer> seq1 = new ArrayList<>();
        List<Integer> seq2 = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            seq1.add(rng1.nextInt(1000));
            seq2.add(rng2.nextInt(1000));
        }

        assertThat(seq1).isNotEqualTo(seq2);
    }

    @Test
    void shuffle_should_be_deterministic_with_same_seed() {
        SeededRandom rng1 = new SeededRandom(99L);
        SeededRandom rng2 = new SeededRandom(99L);

        List<Integer> list1 = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        List<Integer> list2 = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

        rng1.shuffle(list1);
        rng2.shuffle(list2);

        assertThat(list1).isEqualTo(list2);
    }

    @Test
    void pick_one_should_return_element_from_list() {
        SeededRandom rng = new SeededRandom(42L);
        List<String> list = List.of("a", "b", "c");

        String picked = rng.pickOne(list);

        assertThat(list).contains(picked);
    }

    @Test
    void pick_one_should_throw_on_empty_list() {
        SeededRandom rng = new SeededRandom(42L);
        List<String> emptyList = List.of();

        assertThatThrownBy(() -> rng.pickOne(emptyList))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Cannot pick from empty list");
    }

    @Test
    void default_constructor_should_use_current_time_as_seed() {
        long before = System.currentTimeMillis();
        SeededRandom rng = new SeededRandom();
        long after = System.currentTimeMillis();

        assertThat(rng.getSeed()).isBetween(before, after);
    }
}
