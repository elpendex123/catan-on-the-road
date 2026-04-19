package com.enrique.catanontheroad.game;

import com.enrique.catanontheroad.game.card.Metropolis;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private final String name;
    private final Hand hand;

    // Player area
    private int roadCount;
    private int uncoveredSettlementCount;
    private int coveredSettlementCount; // Under cities
    private int uncoveredCityCount;
    private int coveredCityCount; // Under metropolises
    private int knightCount;
    private final List<Metropolis> metropolises;

    // Bonus tracking
    private boolean hasLongestRoute;
    private boolean hasLargestArmy;

    public Player(String name) {
        this.name = name;
        this.hand = new Hand();
        this.roadCount = 0;
        this.uncoveredSettlementCount = 0;
        this.coveredSettlementCount = 0;
        this.uncoveredCityCount = 0;
        this.coveredCityCount = 0;
        this.knightCount = 0;
        this.metropolises = new ArrayList<>();
        this.hasLongestRoute = false;
        this.hasLargestArmy = false;
    }

    public String getName() {
        return name;
    }

    public Hand getHand() {
        return hand;
    }

    // Road methods
    public int getRoadCount() {
        return roadCount;
    }

    public void addRoad() {
        roadCount++;
    }

    // Settlement methods
    public int getUncoveredSettlementCount() {
        return uncoveredSettlementCount;
    }

    public int getTotalSettlementCount() {
        return uncoveredSettlementCount + coveredSettlementCount;
    }

    public void addSettlement() {
        uncoveredSettlementCount++;
    }

    public boolean hasUncoveredSettlement() {
        return uncoveredSettlementCount > 0;
    }

    // City methods
    public int getUncoveredCityCount() {
        return uncoveredCityCount;
    }

    public int getTotalCityCount() {
        return uncoveredCityCount + coveredCityCount;
    }

    public void addCity() {
        if (uncoveredSettlementCount <= 0) {
            throw new IllegalStateException("Cannot build city without uncovered settlement");
        }
        uncoveredSettlementCount--;
        coveredSettlementCount++;
        uncoveredCityCount++;
    }

    public boolean hasUncoveredCity() {
        return uncoveredCityCount > 0;
    }

    // Knight methods
    public int getKnightCount() {
        return knightCount;
    }

    public void addKnight() {
        knightCount++;
    }

    // Metropolis methods
    public List<Metropolis> getMetropolises() {
        return new ArrayList<>(metropolises);
    }

    public int getMetropolisCount() {
        return metropolises.size();
    }

    public void addMetropolis(Metropolis metropolis) {
        if (uncoveredCityCount <= 0) {
            throw new IllegalStateException("Cannot build metropolis without uncovered city");
        }
        uncoveredCityCount--;
        coveredCityCount++;
        metropolises.add(metropolis);
    }

    // Bonus methods
    public boolean hasLongestRoute() {
        return hasLongestRoute;
    }

    public void setLongestRoute(boolean hasLongestRoute) {
        this.hasLongestRoute = hasLongestRoute;
    }

    public boolean hasLargestArmy() {
        return hasLargestArmy;
    }

    public void setLargestArmy(boolean hasLargestArmy) {
        this.hasLargestArmy = hasLargestArmy;
    }

    // Victory points calculation
    public int calculateVictoryPoints() {
        int vp = 0;
        vp += uncoveredSettlementCount * 1;
        vp += uncoveredCityCount * 2;
        vp += metropolises.size() * 3;
        if (hasLongestRoute) vp += 2;
        if (hasLargestArmy) vp += 2;
        return vp;
    }

    // Harvest bonus calculation
    public int calculateHarvestBonus() {
        int bonus = 0;
        bonus += uncoveredCityCount; // +1 per uncovered city
        for (Metropolis m : metropolises) {
            bonus += m.harvestBonus(); // +2 for A, +1 for B
        }
        return bonus;
    }

    // Substitution ratio based on road count
    public int getSubstitutionRatio() {
        if (roadCount >= 4) return 1;
        if (roadCount == 3) return 2;
        if (roadCount == 2) return 3;
        return 4; // 1 road or less
    }

    // Robber threshold
    public int getRobberThreshold() {
        return 7 + knightCount;
    }
}
