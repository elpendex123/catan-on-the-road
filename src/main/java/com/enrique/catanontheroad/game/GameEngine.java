package com.enrique.catanontheroad.game;

import com.enrique.catanontheroad.game.card.ResourceType;
import com.enrique.catanontheroad.game.phase.ActionPhase;
import com.enrique.catanontheroad.game.phase.HarvestPhase;

import java.util.List;

public class GameEngine {

    private final Game game;
    private final HarvestPhase harvestPhase;
    private ActionPhase currentActionPhase;

    public GameEngine(Game game) {
        this.game = game;
        this.harvestPhase = new HarvestPhase();
        this.currentActionPhase = null;
    }

    public Game getGame() {
        return game;
    }

    public List<HarvestPhase.HarvestResult> executeHarvestPhase() {
        List<HarvestPhase.HarvestResult> results = harvestPhase.execute(game);
        currentActionPhase = new ActionPhase();
        return results;
    }

    public ActionPhase getCurrentActionPhase() {
        return currentActionPhase;
    }

    public boolean isTradeAvailable() {
        return currentActionPhase != null && currentActionPhase.isTradeAvailable();
    }

    public boolean isSubstituteAvailable() {
        return currentActionPhase != null && currentActionPhase.isSubstituteAvailable();
    }

    public void markTradeUsed() {
        if (currentActionPhase != null) {
            currentActionPhase.markTradeUsed();
        }
    }

    public void markSubstituteUsed() {
        if (currentActionPhase != null) {
            currentActionPhase.markSubstituteUsed();
        }
    }

    public void endTurn() {
        game.advanceToNextPlayer();
        currentActionPhase = null;
    }

    public boolean checkWinCondition() {
        return game.checkWinCondition();
    }

    public Player getCurrentPlayer() {
        return game.getCurrentPlayer();
    }

    public List<Player> getPlayers() {
        return game.getPlayers();
    }

    public void updateBonuses() {
        game.updateBonuses();
    }

    // Draw resources for a player
    public List<ResourceType> drawResources(Player player, int count) {
        List<ResourceType> drawn = game.getResourceDeck().draw(count);
        player.getHand().add(drawn);
        return drawn;
    }

    // Discard resources from a player
    public void discardResources(Player player, ResourceType type, int count) {
        for (int i = 0; i < count; i++) {
            player.getHand().remove(type);
            game.getResourceDeck().discard(type);
        }
    }
}
