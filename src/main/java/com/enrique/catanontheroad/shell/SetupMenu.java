package com.enrique.catanontheroad.shell;

import com.enrique.catanontheroad.game.card.MetropolisSide;
import com.enrique.catanontheroad.service.GameService;
import com.enrique.catanontheroad.shell.display.AnsiColors;
import com.enrique.catanontheroad.shell.input.MenuPrompt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SetupMenu {

    private static final Logger log = LoggerFactory.getLogger(SetupMenu.class);

    private final GameService gameService;

    public SetupMenu(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Run the setup flow. Returns true if game was successfully created, false if user backed out.
     */
    public boolean run(MenuPrompt prompt) {
        List<String> names = new ArrayList<>();

        // Step 1-3: Collect player names
        for (int i = 0; i < 3; i++) {
            boolean showBack = i > 0;
            String name = prompt.promptText(
                "Enter name for Player " + (i + 1) + ":",
                showBack, true
            );

            if (name.equals("Q")) return false;
            if (name.equals("B")) {
                i -= 2; // Go back one step (loop will increment)
                names.remove(names.size() - 1);
                continue;
            }

            names.add(name);
        }

        // Step 4: Metropolis side
        MetropolisSide side = null;
        while (side == null) {
            System.out.println("\nMetropolis side for this game?");
            var selection = prompt.promptMenu(
                List.of(
                    MenuPrompt.MenuOption.enabled("1", "A side — all metropolises give +2 harvest draws, no special abilities"),
                    MenuPrompt.MenuOption.enabled("2", "B side — all metropolises give +1 harvest draw plus a unique ability per card")
                ),
                true, true
            );

            if (selection.isQuit()) return false;
            if (selection.isBack()) {
                // Go back to player 3 name - but that requires restarting. Simplify: restart from step 1.
                return run(prompt);
            }

            if (selection.choice().equals("1")) side = MetropolisSide.A;
            else if (selection.choice().equals("2")) side = MetropolisSide.B;
        }

        // Step 5: Seed
        long seed;
        while (true) {
            String seedInput = prompt.promptText("Enter seed (blank for random, or any number):", true, true, true);

            if (seedInput.equals("Q")) return false;
            if (seedInput.equals("B")) {
                // Go back to metropolis side selection
                side = null;
                return run(prompt); // restart
            }

            if (seedInput.isBlank()) {
                seed = System.currentTimeMillis();
                break;
            }

            try {
                seed = Long.parseLong(seedInput);
                break;
            } catch (NumberFormatException e) {
                System.out.println(AnsiColors.red("Invalid seed. Enter a number or leave blank."));
            }
        }

        // Create the game
        gameService.createGame(names, side, seed);
        log.info("Game created: players={}, side={}, seed={}", names, side, seed);

        // Show summary
        prompt.clearScreen();
        System.out.println(AnsiColors.bold("=== Game Setup Complete ==="));
        System.out.println();
        System.out.println("Players:");
        for (int i = 0; i < names.size(); i++) {
            String marker = (i == gameService.getGame().getCurrentPlayerIndex()) ? " (starting player)" : "";
            System.out.println("  " + (i + 1) + ". " + names.get(i) + marker);
        }
        System.out.println("\nMetropolis side: " + side);
        System.out.println("Seed: " + seed);
        System.out.println("\nStarting player: " + gameService.getCurrentPlayer().getName());
        System.out.println();
        prompt.promptEnter("Press ENTER to begin...");

        return true;
    }
}
