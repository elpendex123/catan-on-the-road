package com.enrique.catanontheroad.game;

import com.enrique.catanontheroad.game.card.ResourceType;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Hand {

    private final Map<ResourceType, Integer> cards;

    public Hand() {
        this.cards = new EnumMap<>(ResourceType.class);
        for (ResourceType type : ResourceType.values()) {
            cards.put(type, 0);
        }
    }

    public void add(ResourceType type) {
        cards.merge(type, 1, Integer::sum);
    }

    public void add(ResourceType type, int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Cannot add negative count");
        }
        cards.merge(type, count, Integer::sum);
    }

    public void add(List<ResourceType> types) {
        for (ResourceType type : types) {
            add(type);
        }
    }

    public void remove(ResourceType type) {
        remove(type, 1);
    }

    public void remove(ResourceType type, int count) {
        int current = cards.get(type);
        if (count > current) {
            throw new IllegalArgumentException(
                "Cannot remove " + count + " " + type + " cards, only have " + current
            );
        }
        cards.put(type, current - count);
    }

    public int count(ResourceType type) {
        return cards.get(type);
    }

    public int total() {
        return cards.values().stream().mapToInt(Integer::intValue).sum();
    }

    public boolean has(ResourceType type) {
        return count(type) > 0;
    }

    public boolean has(ResourceType type, int count) {
        return count(type) >= count;
    }

    public boolean canAfford(Map<ResourceType, Integer> cost) {
        for (Map.Entry<ResourceType, Integer> entry : cost.entrySet()) {
            if (count(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    public void pay(Map<ResourceType, Integer> cost) {
        if (!canAfford(cost)) {
            throw new IllegalArgumentException("Cannot afford cost: " + cost);
        }
        for (Map.Entry<ResourceType, Integer> entry : cost.entrySet()) {
            remove(entry.getKey(), entry.getValue());
        }
    }

    public Map<ResourceType, Integer> getCards() {
        return new EnumMap<>(cards);
    }

    public boolean isEmpty() {
        return total() == 0;
    }
}
