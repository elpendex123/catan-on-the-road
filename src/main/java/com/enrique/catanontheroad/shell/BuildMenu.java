package com.enrique.catanontheroad.shell;

import com.enrique.catanontheroad.game.Game;
import com.enrique.catanontheroad.game.Player;
import com.enrique.catanontheroad.game.action.BuildAction;
import com.enrique.catanontheroad.game.card.*;
import com.enrique.catanontheroad.game.event.EventResolver;
import com.enrique.catanontheroad.service.GameService;
import com.enrique.catanontheroad.shell.display.AnsiColors;
import com.enrique.catanontheroad.shell.display.HandRenderer;
import com.enrique.catanontheroad.shell.input.MenuPrompt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class BuildMenu {

    private static final Logger log = LoggerFactory.getLogger(BuildMenu.class);

    private final GameService gameService;
    private final HandRenderer handRenderer;

    public BuildMenu(GameService gameService) {
        this.gameService = gameService;
        this.handRenderer = new HandRenderer();
    }

    /**
     * Returns true if user chose Quit.
     */
    public boolean run(MenuPrompt prompt) {
        Player player = gameService.getCurrentPlayer();
        Game game = gameService.getGame();
        BuildAction buildAction = gameService.getBuildAction();

        prompt.clearScreen();
        System.out.println("=== Build ===");

        var roadCheck = buildAction.canAffordRoad(player);
        var settlCheck = buildAction.canAffordSettlement(player);
        var cityCheck = buildAction.canAffordCity(player);
        var knightCheck = buildAction.canAffordKnight(player);
        var metroCheck = buildAction.canAffordMetropolis(player, game);

        boolean roadInRow = game.getBuildingDeck().hasTypeInRow(BuildingType.ROAD);
        boolean settlInRow = game.getBuildingDeck().hasTypeInRow(BuildingType.SETTLEMENT);
        boolean cityInRow = game.getBuildingDeck().hasTypeInRow(BuildingType.CITY);
        boolean knightInRow = game.getBuildingDeck().hasTypeInRow(BuildingType.KNIGHT);

        var options = List.of(
            option("1", "Road          (1 brick, 1 wood)", roadCheck, roadInRow),
            option("2", "Settlement    (1 brick, 1 wood, 1 wool, 1 wheat)", settlCheck, settlInRow),
            option("3", "City          (2 wheat, 3 ore)", cityCheck, cityInRow),
            option("4", "Knight        (1 wool, 1 ore)", knightCheck, knightInRow),
            option("5", "Metropolis    (3 wool, 1 ore)", metroCheck, true)
        );

        var selection = prompt.promptMenu(options, true, true);

        if (selection.isBack()) return false;
        if (selection.isQuit()) return true;

        BuildAction.BuildResult result = null;
        boolean settlementBuilt = false;

        switch (selection.choice()) {
            case "1" -> {
                result = buildAction.buildRoad(player, game);
                if (result.success()) log.info("{} built a road", player.getName());
            }
            case "2" -> {
                result = buildAction.buildSettlement(player, game);
                if (result.success()) {
                    log.info("{} built a settlement", player.getName());
                    settlementBuilt = true;
                }
            }
            case "3" -> {
                result = buildAction.buildCity(player, game);
                if (result.success()) log.info("{} built a city", player.getName());
            }
            case "4" -> {
                result = buildAction.buildKnight(player, game);
                if (result.success()) log.info("{} built a knight", player.getName());
            }
            case "5" -> {
                return buildMetropolis(prompt);
            }
        }

        if (result != null) {
            if (result.success()) {
                System.out.println(AnsiColors.green(result.message()));
                if (settlementBuilt) {
                    boolean quit = resolveEvent(prompt);
                    if (quit) return true;
                }
            } else {
                System.out.println(AnsiColors.red(result.message()));
            }
            prompt.promptEnter("\nPress ENTER to continue...");
        }

        return false;
    }

    private MenuPrompt.MenuOption option(String key, String label,
                                         BuildAction.AffordabilityCheck check, boolean inRow) {
        if (!check.affordable()) {
            return MenuPrompt.MenuOption.disabled(key, label, check.reason());
        }
        if (!inRow) {
            return MenuPrompt.MenuOption.disabled(key, label, "not available in building row");
        }
        return MenuPrompt.MenuOption.enabled(key, label + "    [affordable]");
    }

    private boolean buildMetropolis(MenuPrompt prompt) {
        Game game = gameService.getGame();
        Player player = gameService.getCurrentPlayer();
        BuildAction buildAction = gameService.getBuildAction();

        var metroCheck = buildAction.canAffordMetropolis(player, game);
        if (!metroCheck.affordable()) {
            System.out.println(AnsiColors.red(metroCheck.reason()));
            prompt.promptEnter("\nPress ENTER to continue...");
            return false;
        }

        var available = game.getMetropolisStack().getAvailable();
        if (available.isEmpty()) {
            System.out.println("All metropolises have been built.");
            prompt.promptEnter("\nPress ENTER to continue...");
            return false;
        }

        MetropolisSide side = game.getMetropolisSide();
        System.out.println("=== Build Metropolis (" + side + ") ===");
        System.out.println("Available metropolises (" + available.size() + " remaining):\n");

        List<MenuPrompt.MenuOption> options = new ArrayList<>();
        for (int i = 0; i < available.size(); i++) {
            Metropolis m = available.get(i);
            System.out.println("[" + (i + 1) + "] " + m.type().name().replace('_', ' ') + " METROPOLIS");
            System.out.println("    Cost: 3 wool, 1 ore");
            if (side == MetropolisSide.A) {
                System.out.println("    +2 resources per Harvest phase");
            } else {
                System.out.println("    +1 resource per Harvest phase");
                printBMetropolisAbility(m.type());
            }
            System.out.println("    Worth: 3 VPs\n");
            options.add(MenuPrompt.MenuOption.enabled(String.valueOf(i + 1), m.type().name().replace('_', ' ')));
        }

        var selection = prompt.promptMenu(options, true, true);
        if (selection.isBack()) return false;
        if (selection.isQuit()) return true;

        int index = Integer.parseInt(selection.choice()) - 1;
        Metropolis chosen = game.getMetropolisStack().take(index);
        var result = buildAction.buildMetropolis(player, game, chosen);

        if (result.success()) {
            log.info("{} built {} metropolis", player.getName(), chosen.type());
            System.out.println(AnsiColors.green(result.message()));
            if (!result.bonusDraws().isEmpty()) {
                System.out.print("Bonus draws: ");
                for (int i = 0; i < result.bonusDraws().size(); i++) {
                    if (i > 0) System.out.print(", ");
                    System.out.print(AnsiColors.colorResource(result.bonusDraws().get(i)));
                }
                System.out.println();
            }
        } else {
            System.out.println(AnsiColors.red(result.message()));
        }

        prompt.promptEnter("\nPress ENTER to continue...");
        return false;
    }

    private void printBMetropolisAbility(MetropolisType type) {
        switch (type) {
            case ROAD -> System.out.println("    On build: draw 1 resource per road you have built");
            case LONGEST_ROUTE -> System.out.println("    Wins all ties for Longest Route");
            case KNIGHT -> System.out.println("    On build: draw 1 resource per knight you have built");
            case LARGEST_ARMY -> System.out.println("    Wins all ties for Largest Army");
        }
    }

    private boolean resolveEvent(MenuPrompt prompt) {
        EventResolver resolver = gameService.getEventResolver();
        Game game = gameService.getGame();

        EventCard event = resolver.drawEventCard(game);
        log.info("Event card drawn: {}", event.name());

        System.out.println();
        System.out.println(AnsiColors.bold("=== Event card drawn: " + event.name().toUpperCase() + " ==="));

        if (event instanceof EventCard.Robber) {
            return resolveRobber(prompt, resolver);
        } else if (event instanceof EventCard.Abundance) {
            return resolveAbundance(resolver);
        } else if (event instanceof EventCard.Charity) {
            return resolveCharity(prompt, resolver);
        } else if (event instanceof EventCard.Solstice) {
            return resolveSolstice(resolver);
        } else if (event instanceof EventCard.Subsidy) {
            return resolveSubsidy(resolver);
        }
        throw new IllegalStateException("Unknown event card type: " + event.name());
    }

    private boolean resolveRobber(MenuPrompt prompt, EventResolver resolver) {
        Game game = gameService.getGame();
        System.out.println("Each player with more than (7 + their knight count) resource cards");
        System.out.println("must discard half their hand, rounded down.\n");

        var effects = resolver.calculateRobberEffects(game.getPlayers());

        for (Player p : game.getPlayers()) {
            int threshold = p.getRobberThreshold();
            int handSize = p.getHand().total();
            boolean mustDiscard = effects.stream().anyMatch(e -> e.player() == p);
            System.out.printf("%s: %d cards, %d knights. Threshold: %d. %s%n",
                p.getName(), handSize, p.getKnightCount(), threshold,
                mustDiscard ? "Must discard " + (handSize / 2) + " cards." : "No discard.");
        }

        if (effects.isEmpty()) {
            System.out.println("\nNo one needs to discard.");
            return false;
        }

        for (var effect : effects) {
            prompt.showPassDevice(effect.player().getName());
            boolean quit = doRobberDiscard(prompt, resolver, effect);
            if (quit) return true;
        }

        // Return to current player
        prompt.showPassDevice(gameService.getCurrentPlayer().getName());
        return false;
    }

    private boolean doRobberDiscard(MenuPrompt prompt, EventResolver resolver, EventResolver.RobberEffect effect) {
        Player player = effect.player();
        int toDiscard = effect.discardCount();
        List<ResourceType> discards = new ArrayList<>();

        System.out.println(player.getName() + ", you must discard " + toDiscard + " cards.\n");

        for (int i = 0; i < toDiscard; i++) {
            System.out.println(handRenderer.render(player));
            System.out.println("Discard card " + (i + 1) + " of " + toDiscard + ":");

            List<MenuPrompt.MenuOption> options = new ArrayList<>();
            int idx = 1;
            for (ResourceType type : ResourceType.values()) {
                if (player.getHand().count(type) > 0) {
                    options.add(MenuPrompt.MenuOption.enabled(
                        String.valueOf(idx), AnsiColors.colorResource(type) + " (x" + player.getHand().count(type) + ")"
                    ));
                }
                idx++;
            }

            var selection = prompt.promptMenu(options, false, true);
            if (selection.isQuit()) return true;

            int typeIndex = Integer.parseInt(selection.choice()) - 1;
            ResourceType chosen = ResourceType.values()[typeIndex];
            discards.add(chosen);
            player.getHand().remove(chosen);
        }

        // Put discards into resource deck discard pile
        for (ResourceType type : discards) {
            gameService.getGame().getResourceDeck().discard(type);
        }

        log.debug("{} discarded {} cards via Robber", player.getName(), toDiscard);
        System.out.println(AnsiColors.green("Discarded " + toDiscard + " cards."));
        prompt.promptEnter("\nPress ENTER to continue...");
        return false;
    }

    private boolean resolveAbundance(EventResolver resolver) {
        System.out.println("Each player except Longest Route and Largest Army holders draws 2 cards.\n");
        var drawn = resolver.executeAbundance(gameService.getGame());

        for (var entry : drawn.entrySet()) {
            StringBuilder sb = new StringBuilder();
            sb.append(entry.getKey().getName()).append(" drew: ");
            for (int i = 0; i < entry.getValue().size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(AnsiColors.colorResource(entry.getValue().get(i)));
            }
            System.out.println(sb);
        }

        List<Player> excluded = gameService.getPlayers().stream()
            .filter(p -> !drawn.containsKey(p))
            .toList();
        for (Player p : excluded) {
            System.out.println(p.getName() + " is excluded (holds a bonus card).");
        }

        log.info("Abundance resolved: {} players drew cards", drawn.size());
        return false;
    }

    private boolean resolveCharity(MenuPrompt prompt, EventResolver resolver) {
        Game game = gameService.getGame();
        var effect = resolver.calculateCharityEffect(game.getPlayers());

        System.out.println("Players with most VP give 1 resource each to players with fewest VP.\n");

        switch (effect.charityCase()) {
            case ALL_TIED -> {
                System.out.println("All players are tied on VP. Nothing happens.");
                log.info("Charity: all tied, no effect");
            }
            case IMPOSSIBLE -> {
                System.out.println("Unusual tie state — no effect.");
                log.warn("Charity: impossible case triggered (2+ top AND 2+ bottom)");
            }
            case ONE_TOP_ONE_BOTTOM -> {
                Player giver = effect.givers().get(0);
                Player receiver = effect.receivers().get(0);
                if (giver.getHand().isEmpty()) {
                    System.out.println(giver.getName() + " has no cards to give. Skipped.");
                } else {
                    boolean quit = doCharityGive(prompt, resolver, giver, receiver);
                    if (quit) return true;
                }
            }
            case MULTIPLE_TOP_ONE_BOTTOM -> {
                Player receiver = effect.receivers().get(0);
                for (Player giver : effect.givers()) {
                    if (giver.getHand().isEmpty()) {
                        System.out.println(giver.getName() + " has no cards to give. Skipped.");
                        continue;
                    }
                    boolean quit = doCharityGive(prompt, resolver, giver, receiver);
                    if (quit) return true;
                }
            }
            case ONE_TOP_MULTIPLE_BOTTOM -> {
                Player giver = effect.givers().get(0);
                List<Player> receivers = effect.receivers();
                if (giver.getHand().isEmpty()) {
                    System.out.println(giver.getName() + " has no cards to give. Skipped.");
                } else if (giver.getHand().total() < receivers.size()) {
                    // Giver doesn't have enough cards for all receivers
                    System.out.println(giver.getName() + " doesn't have enough cards for all receivers. Giving what they can.");
                    for (int i = 0; i < Math.min(giver.getHand().total(), receivers.size()); i++) {
                        boolean quit = doCharityGive(prompt, resolver, giver, receivers.get(i));
                        if (quit) return true;
                    }
                } else {
                    // Giver picks N resources, randomly assigned
                    boolean quit = doCharityCaseC(prompt, resolver, giver, receivers);
                    if (quit) return true;
                }
            }
        }

        log.info("Charity resolved: case {}", effect.charityCase());
        return false;
    }

    private boolean doCharityGive(MenuPrompt prompt, EventResolver resolver, Player giver, Player receiver) {
        prompt.showPassDevice(giver.getName());
        System.out.println(giver.getName() + ", give 1 resource to " + receiver.getName() + ".\n");
        System.out.println(handRenderer.render(giver));

        List<MenuPrompt.MenuOption> options = new ArrayList<>();
        int idx = 1;
        for (ResourceType type : ResourceType.values()) {
            if (giver.getHand().count(type) > 0) {
                options.add(MenuPrompt.MenuOption.enabled(String.valueOf(idx), AnsiColors.colorResource(type)));
            }
            idx++;
        }

        var selection = prompt.promptMenu(options, false, true);
        if (selection.isQuit()) return true;

        int typeIndex = Integer.parseInt(selection.choice()) - 1;
        ResourceType chosen = ResourceType.values()[typeIndex];
        resolver.executeCharityGive(giver, chosen, receiver);

        System.out.println(AnsiColors.green(
            giver.getName() + " gave 1 " + AnsiColors.colorResource(chosen) + " to " + receiver.getName() + "."));
        prompt.promptEnter("\nPress ENTER to continue...");
        return false;
    }

    private boolean doCharityCaseC(MenuPrompt prompt, EventResolver resolver, Player giver, List<Player> receivers) {
        prompt.showPassDevice(giver.getName());
        System.out.println(giver.getName() + ", pick " + receivers.size() + " resources to give away.\n");
        System.out.println(handRenderer.render(giver));

        List<ResourceType> chosenResources = new ArrayList<>();
        for (int i = 0; i < receivers.size(); i++) {
            System.out.println("Pick resource " + (i + 1) + " of " + receivers.size() + ":");

            List<MenuPrompt.MenuOption> options = new ArrayList<>();
            int idx = 1;
            for (ResourceType type : ResourceType.values()) {
                if (giver.getHand().count(type) > 0) {
                    options.add(MenuPrompt.MenuOption.enabled(String.valueOf(idx), AnsiColors.colorResource(type)));
                }
                idx++;
            }

            var selection = prompt.promptMenu(options, false, true);
            if (selection.isQuit()) return true;

            int typeIndex = Integer.parseInt(selection.choice()) - 1;
            ResourceType chosen = ResourceType.values()[typeIndex];
            chosenResources.add(chosen);
            giver.getHand().remove(chosen); // Temporarily remove for next pick display
        }

        // Put them back so executeCharityCaseC can remove them
        for (ResourceType type : chosenResources) {
            giver.getHand().add(type);
        }

        var assignments = resolver.executeCharityCaseC(giver, chosenResources, receivers, gameService.getGame());

        System.out.println("\nRandom assignment:");
        for (var entry : assignments.entrySet()) {
            System.out.println("  " + entry.getKey().getName() + " received 1 "
                + AnsiColors.colorResource(entry.getValue()));
        }

        prompt.promptEnter("\nPress ENTER to continue...");
        return false;
    }

    private boolean resolveSolstice(EventResolver resolver) {
        System.out.println("Each player draws 1 resource card. Event deck reshuffled.\n");
        var result = resolver.executeSolstice(gameService.getGame());

        for (var entry : result.drawn().entrySet()) {
            System.out.println(entry.getKey().getName() + " drew: "
                + AnsiColors.colorResource(entry.getValue()));
        }

        log.info("Solstice resolved: event deck reshuffled");
        return false;
    }

    private boolean resolveSubsidy(EventResolver resolver) {
        System.out.println("Each player draws 1 resource card per uncovered settlement.\n");
        var result = resolver.executeSubsidy(gameService.getGame());

        for (var entry : result.drawn().entrySet()) {
            Player player = entry.getKey();
            List<ResourceType> cards = entry.getValue();
            if (cards.isEmpty()) {
                System.out.println(player.getName() + ": no uncovered settlements.");
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append(player.getName()).append(" (").append(player.getUncoveredSettlementCount())
                  .append(" settlements) drew: ");
                for (int i = 0; i < cards.size(); i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(AnsiColors.colorResource(cards.get(i)));
                }
                System.out.println(sb);
            }
        }

        log.info("Subsidy resolved");
        return false;
    }
}
