package com.enrique.catanontheroad.service;

import com.enrique.catanontheroad.game.Game;
import com.enrique.catanontheroad.game.GameEngine;
import com.enrique.catanontheroad.game.Player;
import com.enrique.catanontheroad.game.action.BuildAction;
import com.enrique.catanontheroad.game.action.SubstituteAction;
import com.enrique.catanontheroad.game.action.TradeAction;
import com.enrique.catanontheroad.game.card.MetropolisSide;
import com.enrique.catanontheroad.game.event.EventResolver;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {

    private GameEngine engine;
    private BuildAction buildAction;
    private TradeAction tradeAction;
    private SubstituteAction substituteAction;
    private EventResolver eventResolver;
    private long seed;

    public void createGame(List<String> playerNames, MetropolisSide side, long seed) {
        this.seed = seed;
        Game game = new Game(playerNames, side, seed);
        this.engine = new GameEngine(game);
        this.buildAction = new BuildAction();
        this.tradeAction = new TradeAction();
        this.substituteAction = new SubstituteAction();
        this.eventResolver = new EventResolver();
    }

    public boolean hasActiveGame() {
        return engine != null;
    }

    public GameEngine getEngine() {
        return engine;
    }

    public Game getGame() {
        return engine.getGame();
    }

    public BuildAction getBuildAction() {
        return buildAction;
    }

    public TradeAction getTradeAction() {
        return tradeAction;
    }

    public SubstituteAction getSubstituteAction() {
        return substituteAction;
    }

    public EventResolver getEventResolver() {
        return eventResolver;
    }

    public long getSeed() {
        return seed;
    }

    public Player getCurrentPlayer() {
        return engine.getCurrentPlayer();
    }

    public List<Player> getPlayers() {
        return engine.getPlayers();
    }

    public void endGame() {
        if (engine != null) {
            engine.getGame().endGame();
        }
    }

    public void clearGame() {
        engine = null;
        buildAction = null;
        tradeAction = null;
        substituteAction = null;
        eventResolver = null;
    }
}
