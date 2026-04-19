package com.enrique.catanontheroad.shell;

import com.enrique.catanontheroad.service.GameService;
import com.enrique.catanontheroad.shell.display.BoardRenderer;
import com.enrique.catanontheroad.shell.display.HandRenderer;
import com.enrique.catanontheroad.shell.display.PlayerAreaRenderer;
import com.enrique.catanontheroad.shell.input.MenuPrompt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TurnMenu {

    private static final Logger log = LoggerFactory.getLogger(TurnMenu.class);

    private final GameService gameService;
    private final BuildMenu buildMenu;
    private final TradeMenu tradeMenu;
    private final SubstituteMenu substituteMenu;
    private final PlayerAreaRenderer playerAreaRenderer;
    private final BoardRenderer boardRenderer;
    private final HandRenderer handRenderer;

    public TurnMenu(GameService gameService, BuildMenu buildMenu,
                    TradeMenu tradeMenu, SubstituteMenu substituteMenu) {
        this.gameService = gameService;
        this.buildMenu = buildMenu;
        this.tradeMenu = tradeMenu;
        this.substituteMenu = substituteMenu;
        this.playerAreaRenderer = new PlayerAreaRenderer();
        this.boardRenderer = new BoardRenderer();
        this.handRenderer = new HandRenderer();
    }

    /**
     * Run the action phase menu. Returns true if user chose Quit.
     */
    public boolean run(MenuPrompt prompt) {
        var engine = gameService.getEngine();

        while (true) {
            prompt.clearScreen();
            System.out.print(playerAreaRenderer.renderTurnHeader(gameService.getGame()));
            System.out.println();

            boolean tradeAvail = engine.isTradeAvailable();
            boolean subAvail = engine.isSubstituteAvailable();

            var selection = prompt.promptMenu(
                List.of(
                    MenuPrompt.MenuOption.enabled("1", "View board"),
                    MenuPrompt.MenuOption.enabled("2", "View my hand"),
                    tradeAvail
                        ? MenuPrompt.MenuOption.enabled("3", "Trade with another player")
                        : MenuPrompt.MenuOption.disabled("3", "Trade with another player", "already used this turn"),
                    MenuPrompt.MenuOption.enabled("4", "Build"),
                    subAvail
                        ? MenuPrompt.MenuOption.enabled("5", "Substitute resources")
                        : MenuPrompt.MenuOption.disabled("5", "Substitute resources", "already used this turn"),
                    MenuPrompt.MenuOption.enabled("6", "End turn")
                ),
                false, true
            );

            if (selection.isQuit()) {
                log.trace("Quit selected during action phase");
                return true;
            }

            if (selection.isOption()) {
                switch (selection.choice()) {
                    case "1" -> {
                        log.trace("View board selected");
                        viewBoard(prompt);
                    }
                    case "2" -> {
                        log.trace("View hand selected");
                        viewHand(prompt);
                    }
                    case "3" -> {
                        log.trace("Trade selected");
                        boolean quit = tradeMenu.run(prompt);
                        if (quit) return true;
                    }
                    case "4" -> {
                        log.trace("Build selected");
                        boolean quit = buildMenu.run(prompt);
                        if (quit) return true;
                        // Check win after build
                        if (engine.checkWinCondition()) return false;
                    }
                    case "5" -> {
                        log.trace("Substitute selected");
                        boolean quit = substituteMenu.run(prompt);
                        if (quit) return true;
                    }
                    case "6" -> {
                        log.trace("End turn selected");
                        if (prompt.promptYesNo("End your turn?")) {
                            return false;
                        }
                    }
                }
            }
        }
    }

    private void viewBoard(MenuPrompt prompt) {
        prompt.clearScreen();
        System.out.print(boardRenderer.renderBuildingRow(gameService.getGame()));
        System.out.println();
        System.out.print(boardRenderer.renderPlayerAreas(gameService.getGame()));
        System.out.println();
        prompt.promptMenu(List.of(), true, true);
    }

    private void viewHand(MenuPrompt prompt) {
        prompt.clearScreen();
        System.out.print(handRenderer.render(gameService.getCurrentPlayer()));
        System.out.println();
        prompt.promptMenu(List.of(), true, true);
    }
}
