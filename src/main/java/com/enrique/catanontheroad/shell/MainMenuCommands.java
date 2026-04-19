package com.enrique.catanontheroad.shell;

import com.enrique.catanontheroad.service.GameService;
import com.enrique.catanontheroad.shell.display.AnsiColors;
import com.enrique.catanontheroad.shell.display.ScoreboardRenderer;
import com.enrique.catanontheroad.shell.input.MenuPrompt;
import org.jline.reader.LineReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.List;

@ShellComponent
public class MainMenuCommands {

    private static final Logger log = LoggerFactory.getLogger(MainMenuCommands.class);

    private final GameService gameService;
    private final LineReader lineReader;
    private final SetupMenu setupMenu;
    private final TurnMenu turnMenu;
    private final ScoreboardRenderer scoreboardRenderer;

    public MainMenuCommands(GameService gameService, LineReader lineReader,
                            SetupMenu setupMenu, TurnMenu turnMenu) {
        this.gameService = gameService;
        this.lineReader = lineReader;
        this.setupMenu = setupMenu;
        this.turnMenu = turnMenu;
        this.scoreboardRenderer = new ScoreboardRenderer();
    }

    @ShellMethod(value = "Start the game", key = "play")
    public void play() {
        log.info("Game started");
        MenuPrompt prompt = new MenuPrompt(lineReader);

        while (true) {
            prompt.clearScreen();
            System.out.println("=== CATAN: On the Road ===");
            var selection = prompt.promptMenu(
                List.of(MenuPrompt.MenuOption.enabled("1", "New game")),
                false, true
            );

            if (selection.isQuit()) {
                log.info("Quit from main menu");
                return;
            }

            if (selection.isOption() && selection.choice().equals("1")) {
                boolean started = setupMenu.run(prompt);
                if (started) {
                    runGameLoop(prompt);
                    if (gameService.hasActiveGame()) {
                        showFinalScoreboard();
                    }
                    gameService.clearGame();
                }
            }
        }
    }

    private void runGameLoop(MenuPrompt prompt) {
        var engine = gameService.getEngine();

        while (!engine.getGame().isGameEnded()) {
            // Pass-device prompt (skip for first turn of game)
            prompt.showPassDevice(engine.getCurrentPlayer().getName());

            // Harvest phase
            log.info("Turn {} for {}", engine.getGame().getRound(), engine.getCurrentPlayer().getName());
            var harvestResults = engine.executeHarvestPhase();
            displayHarvestResults(harvestResults, prompt);

            // Check win after harvest (bonuses might have shifted)
            if (engine.checkWinCondition()) {
                log.info("{} wins with {} VP!", engine.getCurrentPlayer().getName(),
                    engine.getCurrentPlayer().calculateVictoryPoints());
                break;
            }

            // Action phase
            boolean quit = turnMenu.run(prompt);
            if (quit) {
                gameService.endGame();
                break;
            }

            if (engine.checkWinCondition()) {
                log.info("{} wins with {} VP!", engine.getCurrentPlayer().getName(),
                    engine.getCurrentPlayer().calculateVictoryPoints());
                break;
            }

            // End turn
            engine.endTurn();
        }
    }

    private void displayHarvestResults(
            List<com.enrique.catanontheroad.game.phase.HarvestPhase.HarvestResult> results,
            MenuPrompt prompt) {
        System.out.println(AnsiColors.bold("=== Harvest Phase ==="));
        for (var result : results) {
            StringBuilder sb = new StringBuilder();
            sb.append(result.player().getName()).append(" drew: ");
            for (int i = 0; i < result.cardsDrawn().size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(AnsiColors.colorResource(result.cardsDrawn().get(i)));
            }
            System.out.println(sb);
        }
        System.out.println();
        prompt.promptEnter("Press ENTER to continue...");
    }

    private void showFinalScoreboard() {
        System.out.println(scoreboardRenderer.render(gameService.getGame(), gameService.getSeed()));
    }
}
