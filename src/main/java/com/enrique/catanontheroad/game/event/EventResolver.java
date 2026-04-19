package com.enrique.catanontheroad.game.event;

import com.enrique.catanontheroad.game.Game;
import com.enrique.catanontheroad.game.Player;
import com.enrique.catanontheroad.game.card.EventCard;
import com.enrique.catanontheroad.game.card.ResourceType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventResolver {

    // --- Robber ---

    public record RobberEffect(Player player, int discardCount) {}

    public List<RobberEffect> calculateRobberEffects(List<Player> players) {
        List<RobberEffect> effects = new ArrayList<>();
        for (Player player : players) {
            int threshold = player.getRobberThreshold();
            int handSize = player.getHand().total();
            if (handSize > threshold) {
                int discardCount = handSize / 2;
                effects.add(new RobberEffect(player, discardCount));
            }
        }
        return effects;
    }

    public void executeRobberDiscard(Player player, List<ResourceType> cardsToDiscard, Game game) {
        for (ResourceType type : cardsToDiscard) {
            player.getHand().remove(type);
            game.getResourceDeck().discard(type);
        }
    }

    // --- Abundance ---

    public List<Player> getAbundanceEligiblePlayers(List<Player> players) {
        List<Player> eligible = new ArrayList<>();
        for (Player player : players) {
            if (!player.hasLongestRoute() && !player.hasLargestArmy()) {
                eligible.add(player);
            }
        }
        return eligible;
    }

    public Map<Player, List<ResourceType>> executeAbundance(Game game) {
        List<Player> eligible = getAbundanceEligiblePlayers(game.getPlayers());
        Map<Player, List<ResourceType>> drawn = new java.util.LinkedHashMap<>();
        for (Player player : eligible) {
            List<ResourceType> cards = game.getResourceDeck().draw(2);
            player.getHand().add(cards);
            drawn.put(player, cards);
        }
        return drawn;
    }

    // --- Charity ---

    public enum CharityCase {
        ONE_TOP_ONE_BOTTOM,       // Case A
        MULTIPLE_TOP_ONE_BOTTOM,  // Case B
        ONE_TOP_MULTIPLE_BOTTOM,  // Case C
        ALL_TIED,                 // Case D
        IMPOSSIBLE                // Case F
    }

    public record CharityEffect(CharityCase charityCase, List<Player> givers, List<Player> receivers) {}

    public CharityEffect calculateCharityEffect(List<Player> players) {
        int maxVP = Integer.MIN_VALUE;
        int minVP = Integer.MAX_VALUE;
        for (Player p : players) {
            int vp = p.calculateVictoryPoints();
            if (vp > maxVP) maxVP = vp;
            if (vp < minVP) minVP = vp;
        }

        if (maxVP == minVP) {
            return new CharityEffect(CharityCase.ALL_TIED, List.of(), List.of());
        }

        List<Player> givers = new ArrayList<>();
        List<Player> receivers = new ArrayList<>();
        for (Player p : players) {
            int vp = p.calculateVictoryPoints();
            if (vp == maxVP) givers.add(p);
            if (vp == minVP) receivers.add(p);
        }

        if (givers.size() >= 2 && receivers.size() >= 2) {
            return new CharityEffect(CharityCase.IMPOSSIBLE, List.of(), List.of());
        }

        CharityCase charityCase;
        if (givers.size() == 1 && receivers.size() == 1) {
            charityCase = CharityCase.ONE_TOP_ONE_BOTTOM;
        } else if (givers.size() >= 2) {
            charityCase = CharityCase.MULTIPLE_TOP_ONE_BOTTOM;
        } else {
            charityCase = CharityCase.ONE_TOP_MULTIPLE_BOTTOM;
        }

        return new CharityEffect(charityCase, givers, receivers);
    }

    /**
     * Execute a single charity give from a giver to a receiver.
     * Used for Case A and Case B.
     */
    public void executeCharityGive(Player giver, ResourceType resource, Player receiver) {
        giver.getHand().remove(resource);
        receiver.getHand().add(resource);
    }

    /**
     * Execute Case C: one giver gives 2 resources, randomly assigned to 2 receivers.
     * The giver must choose 2 resources. The game randomly assigns each to a receiver.
     */
    public Map<Player, ResourceType> executeCharityCaseC(
            Player giver, List<ResourceType> resources, List<Player> receivers, Game game) {
        if (resources.size() != receivers.size()) {
            throw new IllegalArgumentException(
                "Must provide exactly " + receivers.size() + " resources for " + receivers.size() + " receivers");
        }

        // Remove resources from giver
        for (ResourceType resource : resources) {
            giver.getHand().remove(resource);
        }

        // Shuffle receivers to randomly assign
        List<Player> shuffledReceivers = new ArrayList<>(receivers);
        game.getRng().shuffle(shuffledReceivers);

        Map<Player, ResourceType> assignments = new java.util.LinkedHashMap<>();
        for (int i = 0; i < shuffledReceivers.size(); i++) {
            Player receiver = shuffledReceivers.get(i);
            ResourceType resource = resources.get(i);
            receiver.getHand().add(resource);
            assignments.put(receiver, resource);
        }

        return assignments;
    }

    // --- Solstice ---

    public record SolsticeResult(Map<Player, ResourceType> drawn) {}

    public SolsticeResult executeSolstice(Game game) {
        List<Player> players = game.getPlayers();
        Map<Player, ResourceType> drawn = new java.util.LinkedHashMap<>();
        for (Player player : players) {
            ResourceType card = game.getResourceDeck().draw();
            player.getHand().add(card);
            drawn.put(player, card);
        }
        game.getEventDeck().reshuffleAll();
        return new SolsticeResult(drawn);
    }

    // --- Subsidy ---

    public record SubsidyResult(Map<Player, List<ResourceType>> drawn) {}

    public SubsidyResult executeSubsidy(Game game) {
        List<Player> players = game.getPlayers();
        Map<Player, List<ResourceType>> drawn = new java.util.LinkedHashMap<>();
        for (Player player : players) {
            int settlementCount = player.getUncoveredSettlementCount();
            if (settlementCount > 0) {
                List<ResourceType> cards = game.getResourceDeck().draw(settlementCount);
                player.getHand().add(cards);
                drawn.put(player, cards);
            } else {
                drawn.put(player, List.of());
            }
        }
        return new SubsidyResult(drawn);
    }

    // --- Dispatch ---

    /**
     * Draw and return an event card from the deck.
     * The caller is responsible for resolving the event using the appropriate method.
     */
    public EventCard drawEventCard(Game game) {
        return game.getEventDeck().draw();
    }
}
