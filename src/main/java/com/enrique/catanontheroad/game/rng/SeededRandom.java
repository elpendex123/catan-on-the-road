package com.enrique.catanontheroad.game.rng;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SeededRandom {

    private final long seed;
    private final Random random;

    public SeededRandom(long seed) {
        this.seed = seed;
        this.random = new Random(seed);
    }

    public SeededRandom() {
        this(System.currentTimeMillis());
    }

    public long getSeed() {
        return seed;
    }

    public int nextInt(int bound) {
        return random.nextInt(bound);
    }

    public <T> void shuffle(List<T> list) {
        Collections.shuffle(list, random);
    }

    public <T> T pickOne(List<T> list) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("Cannot pick from empty list");
        }
        return list.get(nextInt(list.size()));
    }
}
