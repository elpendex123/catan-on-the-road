package com.enrique.catanontheroad.game;

import com.enrique.catanontheroad.game.bonus.LargestArmy;
import com.enrique.catanontheroad.game.bonus.LongestRoute;
import com.enrique.catanontheroad.game.card.MetropolisSide;
import com.enrique.catanontheroad.game.deck.BuildingDeck;
import com.enrique.catanontheroad.game.deck.EventDeck;
import com.enrique.catanontheroad.game.deck.MetropolisStack;
import com.enrique.catanontheroad.game.deck.ResourceDeck;
import com.enrique.catanontheroad.game.rng.SeededRandom;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private static final int WINNING_VP = 7;
    private static final int STARTING_HAND_SIZE = 2;

    private final List<Player> players;
    private final ResourceDeck resourceDeck;
    private final BuildingDeck buildingDeck;
    private final EventDeck eventDeck;
    private final MetropolisStack metropolisStack;
    private final LongestRoute longestRoute;
    private final LargestArmy largestArmy;
    private final MetropolisSide metropolisSide;
    private final SeededRandom rng;

    private int currentPlayerIndex;
    private int round;
    private Player winner;
    private boolean gameEnded;

    public Game(List<String> playerNames, MetropolisSide metropolisSide, long seed) {
        if (playerNames.size() != 3) {
            throw new IllegalArgumentException("Exactly 3 players required");
        }

        this.rng = new SeededRandom(seed);
        this.metropolisSide = metropolisSide;

        // Initialize decks
        this.resourceDeck = new ResourceDeck(rng);
        this.buildingDeck = new BuildingDeck(rng);
        this.eventDeck = new EventDeck(rng);
        this.metropolisStack = new MetropolisStack(metropolisSide);

        // Initialize bonus trackers
        this.longestRoute = new LongestRoute();
        this.largestArmy = new LargestArmy();

        // Initialize players with starting settlement and road
        this.players = new ArrayList<>();
        for (String name : playerNames) {
            Player player = new Player(name);
            player.addSettlement(); // Starting settlement
            player.addRoad();       // Starting road
            players.add(player);
        }

        // Deal starting hands
        for (Player player : players) {
            for (int i = 0; i < STARTING_HAND_SIZE; i++) {
                player.getHand().add(resourceDeck.draw());
            }
        }

        // Determine starting player (random)
        this.currentPlayerIndex = rng.nextInt(players.size());
        this.round = 1;
        this.winner = null;
        this.gameEnded = false;
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void advanceToNextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        if (currentPlayerIndex == 0) {
            round++;
        }
    }

    public int getRound() {
        return round;
    }

    public ResourceDeck getResourceDeck() {
        return resourceDeck;
    }

    public BuildingDeck getBuildingDeck() {
        return buildingDeck;
    }

    public EventDeck getEventDeck() {
        return eventDeck;
    }

    public MetropolisStack getMetropolisStack() {
        return metropolisStack;
    }

    public LongestRoute getLongestRoute() {
        return longestRoute;
    }

    public LargestArmy getLargestArmy() {
        return largestArmy;
    }

    public MetropolisSide getMetropolisSide() {
        return metropolisSide;
    }

    public SeededRandom getRng() {
        return rng;
    }

    public Player getWinner() {
        return winner;
    }

    public boolean isGameEnded() {
        return gameEnded;
    }

    public boolean checkWinCondition() {
        Player current = getCurrentPlayer();
        if (current.calculateVictoryPoints() >= WINNING_VP) {
            winner = current;
            gameEnded = true;
            return true;
        }
        return false;
    }

    public void endGame() {
        gameEnded = true;
    }

    public Player getPlayerByName(String name) {
        return players.stream()
            .filter(p -> p.getName().equals(name))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Player not found: " + name));
    }

    public List<Player> getOtherPlayers(Player current) {
        List<Player> others = new ArrayList<>();
        for (Player p : players) {
            if (p != current) {
                others.add(p);
            }
        }
        return others;
    }

    public void updateBonuses() {
        longestRoute.checkAndUpdate(players, metropolisSide);
        largestArmy.checkAndUpdate(players, metropolisSide);
    }
}
