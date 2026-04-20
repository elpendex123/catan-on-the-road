package com.enrique.catanontheroad.shell;

import com.enrique.catanontheroad.game.Player;
import com.enrique.catanontheroad.game.action.TradeAction;
import com.enrique.catanontheroad.game.card.ResourceType;
import com.enrique.catanontheroad.service.GameService;
import com.enrique.catanontheroad.shell.display.AnsiColors;
import com.enrique.catanontheroad.shell.display.HandRenderer;
import com.enrique.catanontheroad.shell.input.MenuPrompt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TradeMenu {

    private static final Logger log = LoggerFactory.getLogger(TradeMenu.class);

    private final GameService gameService;
    private final HandRenderer handRenderer;

    public TradeMenu(GameService gameService) {
        this.gameService = gameService;
        this.handRenderer = new HandRenderer();
    }

    /**
     * Returns true if user chose Quit.
     */
    public boolean run(MenuPrompt prompt) {
        Player player = gameService.getCurrentPlayer();
        TradeAction tradeAction = gameService.getTradeAction();

        // Step 1: Offer selection
        prompt.clearScreen();
        System.out.println("=== Trade — Step 1 of 4 ===");
        System.out.println("Your hand:");
        System.out.println(handRenderer.render(player));
        System.out.println("Which resource type(s) are you willing to offer? (comma-separated numbers)");

        String offerInput = prompt.promptText("> ", true, true);
        if (offerInput.equals("B")) return false;
        if (offerInput.equals("Q")) return true;

        List<ResourceType> offeredTypes;
        try {
            offeredTypes = parseResourceSelection(offerInput, player);
        } catch (IllegalArgumentException e) {
            System.out.println(AnsiColors.red(e.getMessage()));
            prompt.promptEnter("\nPress ENTER to continue...");
            return false;
        }

        // Step 2: Request selection
        prompt.clearScreen();
        System.out.println("=== Trade — Step 2 of 4 ===");
        System.out.print("You are offering: ");
        System.out.println(formatTypeList(offeredTypes));
        System.out.println("\nWhat do you want in return? (single number)");
        printResourceList();

        String wantInput = prompt.promptText("> ", true, true);
        if (wantInput.equals("B")) return run(prompt); // Back to step 1
        if (wantInput.equals("Q")) return true;

        ResourceType requestedType;
        try {
            int idx = Integer.parseInt(wantInput.trim()) - 1;
            if (idx < 0 || idx >= ResourceType.values().length) {
                throw new IllegalArgumentException("Invalid selection");
            }
            requestedType = ResourceType.values()[idx];
        } catch (NumberFormatException e) {
            System.out.println(AnsiColors.red("Invalid input."));
            prompt.promptEnter("\nPress ENTER to continue...");
            return false;
        }

        // Validate offer
        var validation = tradeAction.validateOffer(player, offeredTypes, requestedType);
        if (!validation.isValid()) {
            System.out.println(AnsiColors.red(validation.message()));
            prompt.promptEnter("\nPress ENTER to continue...");
            return false;
        }

        // Step 3: Partner selection
        prompt.clearScreen();
        System.out.println("=== Trade — Step 3 of 4 ===");
        System.out.println("You offer: " + formatTypeList(offeredTypes));
        System.out.println("You want: " + AnsiColors.colorResource(requestedType));
        System.out.println("\nTrade with whom?");

        List<Player> others = gameService.getGame().getOtherPlayers(player);
        List<MenuPrompt.MenuOption> partnerOptions = new ArrayList<>();
        for (int i = 0; i < others.size(); i++) {
            partnerOptions.add(MenuPrompt.MenuOption.enabled(String.valueOf(i + 1), others.get(i).getName()));
        }

        var partnerSelection = prompt.promptMenu(partnerOptions, true, true);
        if (partnerSelection.isBack()) return run(prompt); // Back to step 1
        if (partnerSelection.isQuit()) return true;

        int partnerIndex = Integer.parseInt(partnerSelection.choice()) - 1;
        Player target = others.get(partnerIndex);

        // Step 4: Confirm
        prompt.clearScreen();
        System.out.println("=== Trade — Step 4 of 4 ===");
        System.out.println("Summary: offer " + formatTypeList(offeredTypes) +
            " to " + target.getName() + " in exchange for 1 " + AnsiColors.colorResource(requestedType) + ".\n");

        var confirmOptions = List.of(
            MenuPrompt.MenuOption.enabled("1", "Propose trade to " + target.getName())
        );
        var confirmSelection = prompt.promptMenu(confirmOptions, true, true);
        if (confirmSelection.isBack()) return run(prompt);
        if (confirmSelection.isQuit()) return true;

        // Auto-decline check
        if (tradeAction.canAutoDecline(target, requestedType)) {
            System.out.println(AnsiColors.red(
                target.getName() + " has no " + requestedType.name().toLowerCase() + " to trade. Trade auto-declined."));
            gameService.getEngine().markTradeUsed();
            log.info("Trade auto-declined: {} has no {}", target.getName(), requestedType);
            prompt.promptEnter("\nPress ENTER to continue...");
            return false;
        }

        // Partner decision
        TradeAction.TradeOffer offer = new TradeAction.TradeOffer(player, offeredTypes, requestedType, target);
        return presentToTarget(prompt, offer);
    }

    private boolean presentToTarget(MenuPrompt prompt, TradeAction.TradeOffer offer) {
        Player target = offer.target();
        Player offerer = offer.offerer();

        prompt.showPassDevice(target.getName());

        System.out.println("=== " + target.getName() + ", " + offerer.getName() + " is offering you a trade ===");
        System.out.println("Your hand:");
        System.out.println(handRenderer.render(target));
        System.out.println(offerer.getName() + " is offering:");
        for (ResourceType type : offer.offeredTypes()) {
            System.out.println("  - " + AnsiColors.colorResource(type));
        }
        System.out.println(offerer.getName() + " wants: 1 " + AnsiColors.colorResource(offer.requestedType()));
        System.out.println();

        boolean accepted = prompt.promptYesNo("Accept?");

        if (!accepted) {
            gameService.getEngine().markTradeUsed();
            prompt.showPassDevice(offerer.getName());
            System.out.println(target.getName() + " declined the trade.");
            log.info("Trade declined by {}", target.getName());
            prompt.promptEnter("\nPress ENTER to return to your turn menu...");
            return false;
        }

        // If multiple types offered, target picks which to receive
        ResourceType chosenType;
        if (offer.offeredTypes().size() == 1) {
            chosenType = offer.offeredTypes().get(0);
        } else {
            System.out.println("\nPick which of " + offerer.getName() + "'s offered types you want to receive:");
            List<MenuPrompt.MenuOption> pickOptions = new ArrayList<>();
            for (int i = 0; i < offer.offeredTypes().size(); i++) {
                ResourceType type = offer.offeredTypes().get(i);
                pickOptions.add(MenuPrompt.MenuOption.enabled(
                    String.valueOf(i + 1), AnsiColors.colorResource(type)));
            }

            var pickSelection = prompt.promptMenu(pickOptions, false, true);
            if (pickSelection.isQuit()) return true;

            int pickIndex = Integer.parseInt(pickSelection.choice()) - 1;
            chosenType = offer.offeredTypes().get(pickIndex);
        }

        // Execute trade
        var result = gameService.getTradeAction().executeTrade(offer, chosenType, gameService.getGame());
        gameService.getEngine().markTradeUsed();

        log.info("Trade accepted: {} gave {} to {}, received {}", offerer.getName(),
            chosenType, target.getName(), offer.requestedType());

        // Show result to offerer
        prompt.showPassDevice(offerer.getName());
        System.out.println("=== Trade accepted ===");
        System.out.println("You gave " + target.getName() + ": 1 " + AnsiColors.colorResource(chosenType));
        System.out.println("You received from " + target.getName() + ": 1 "
            + AnsiColors.colorResource(offer.requestedType()));
        System.out.println(target.getName() + " drew a bonus resource card.");
        prompt.promptEnter("\nPress ENTER to return to your turn menu...");

        return false;
    }

    private List<ResourceType> parseResourceSelection(String input, Player player) {
        String[] parts = input.split(",");
        List<ResourceType> types = new ArrayList<>();
        ResourceType[] allTypes = ResourceType.values();

        for (String part : parts) {
            int idx;
            try {
                idx = Integer.parseInt(part.trim()) - 1;
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number: " + part.trim());
            }
            if (idx < 0 || idx >= allTypes.length) {
                throw new IllegalArgumentException("Invalid selection: " + (idx + 1));
            }
            ResourceType type = allTypes[idx];
            if (!types.contains(type)) {
                types.add(type);
            }
        }

        return types;
    }

    private String formatTypeList(List<ResourceType> types) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < types.size(); i++) {
            if (i > 0) sb.append(" OR ");
            sb.append(AnsiColors.colorResource(types.get(i)));
        }
        return sb.toString();
    }

    private void printResourceList() {
        int idx = 1;
        for (ResourceType type : ResourceType.values()) {
            System.out.println("  " + idx + ". " + AnsiColors.colorResource(type));
            idx++;
        }
    }
}
