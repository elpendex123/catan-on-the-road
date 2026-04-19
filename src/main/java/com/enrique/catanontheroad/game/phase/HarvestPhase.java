package com.enrique.catanontheroad.game.phase;

import com.enrique.catanontheroad.game.Game;
import com.enrique.catanontheroad.game.Player;
import com.enrique.catanontheroad.game.card.ResourceType;

import java.util.ArrayList;
import java.util.List;

public class HarvestPhase {

    public record HarvestResult(Player player, List<ResourceType> cardsDrawn) {}

    public List<HarvestResult> execute(Game game) {
        List<HarvestResult> results = new ArrayList<>();
        List<Player> players = game.getPlayers();
        int currentIndex = game.getCurrentPlayerIndex();

        // All players draw 1 base card, starting with current player
        for (int i = 0; i < players.size(); i++) {
            int playerIndex = (currentIndex + i) % players.size();
            Player player = players.get(playerIndex);

            List<ResourceType> drawn = new ArrayList<>();
            ResourceType baseCard = game.getResourceDeck().draw();
            player.getHand().add(baseCard);
            drawn.add(baseCard);

            results.add(new HarvestResult(player, drawn));
        }

        // Current player draws bonus cards
        Player currentPlayer = game.getCurrentPlayer();
        int bonusCount = currentPlayer.calculateHarvestBonus();

        if (bonusCount > 0) {
            HarvestResult currentResult = results.get(0);
            List<ResourceType> additionalDraws = new ArrayList<>(currentResult.cardsDrawn());

            for (int i = 0; i < bonusCount; i++) {
                ResourceType bonusCard = game.getResourceDeck().draw();
                currentPlayer.getHand().add(bonusCard);
                additionalDraws.add(bonusCard);
            }

            results.set(0, new HarvestResult(currentPlayer, additionalDraws));
        }

        return results;
    }
}
