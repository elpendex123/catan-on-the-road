package com.enrique.catanontheroad.game.deck;

import com.enrique.catanontheroad.game.card.Metropolis;
import com.enrique.catanontheroad.game.card.MetropolisSide;
import com.enrique.catanontheroad.game.card.MetropolisType;

import java.util.ArrayList;
import java.util.List;

public class MetropolisStack {

    private final List<Metropolis> available;
    private final MetropolisSide side;

    public MetropolisStack(MetropolisSide side) {
        this.side = side;
        this.available = new ArrayList<>();
        initializeStack();
    }

    private void initializeStack() {
        available.add(new Metropolis(side, MetropolisType.ROAD));
        available.add(new Metropolis(side, MetropolisType.LONGEST_ROUTE));
        available.add(new Metropolis(side, MetropolisType.KNIGHT));
        available.add(new Metropolis(side, MetropolisType.LARGEST_ARMY));
    }

    public List<Metropolis> getAvailable() {
        return new ArrayList<>(available);
    }

    public Metropolis take(MetropolisType type) {
        for (int i = 0; i < available.size(); i++) {
            if (available.get(i).type() == type) {
                return available.remove(i);
            }
        }
        throw new IllegalStateException("Metropolis of type " + type + " is not available");
    }

    public Metropolis take(int index) {
        if (index < 0 || index >= available.size()) {
            throw new IllegalArgumentException("Invalid index: " + index);
        }
        return available.remove(index);
    }

    public boolean isAvailable(MetropolisType type) {
        return available.stream().anyMatch(m -> m.type() == type);
    }

    public int remainingCount() {
        return available.size();
    }

    public boolean isEmpty() {
        return available.isEmpty();
    }

    public MetropolisSide getSide() {
        return side;
    }
}
