package com.enrique.catanontheroad.shell;

import com.enrique.catanontheroad.game.Player;
import com.enrique.catanontheroad.game.action.SubstituteAction;
import com.enrique.catanontheroad.game.card.ResourceType;
import com.enrique.catanontheroad.service.GameService;
import com.enrique.catanontheroad.shell.display.AnsiColors;
import com.enrique.catanontheroad.shell.display.HandRenderer;
import com.enrique.catanontheroad.shell.input.MenuPrompt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SubstituteMenu {

    private static final Logger log = LoggerFactory.getLogger(SubstituteMenu.class);

    private final GameService gameService;
    private final HandRenderer handRenderer;

    public SubstituteMenu(GameService gameService) {
        this.gameService = gameService;
        this.handRenderer = new HandRenderer();
    }

    /**
     * Returns true if user chose Quit.
     */
    public boolean run(MenuPrompt prompt) {
        Player player = gameService.getCurrentPlayer();
        SubstituteAction subAction = gameService.getSubstituteAction();
        int ratio = subAction.getRatio(player);

        prompt.clearScreen();
        System.out.println("=== Substitute resources ===");
        System.out.println("Your roads: " + player.getRoadCount() + " (substitution ratio " + ratio + ":1)");
        System.out.println("Your hand:");
        System.out.println(handRenderer.render(player));
        System.out.println("Which resource to give up? (need at least " + ratio + " of the chosen type)");

        // Show resource options with enough cards
        List<MenuPrompt.MenuOption> sourceOptions = new ArrayList<>();
        int idx = 1;
        for (ResourceType type : ResourceType.values()) {
            int count = player.getHand().count(type);
            if (count >= ratio) {
                sourceOptions.add(MenuPrompt.MenuOption.enabled(
                    String.valueOf(idx), AnsiColors.colorResource(type) + "  x" + count));
            } else {
                sourceOptions.add(MenuPrompt.MenuOption.disabled(
                    String.valueOf(idx), AnsiColors.colorResource(type) + "  x" + count,
                    "need " + ratio + ", have " + count));
            }
            idx++;
        }

        var sourceSelection = prompt.promptMenu(sourceOptions, true, true);
        if (sourceSelection.isBack()) return false;
        if (sourceSelection.isQuit()) return true;

        int sourceIndex = Integer.parseInt(sourceSelection.choice()) - 1;
        ResourceType sourceType = ResourceType.values()[sourceIndex];

        // Target selection
        System.out.println("\nYou will give up " + ratio + " " + AnsiColors.colorResource(sourceType)
            + ". Which resource do you want?");

        List<MenuPrompt.MenuOption> targetOptions = new ArrayList<>();
        idx = 1;
        for (ResourceType type : ResourceType.values()) {
            if (type != sourceType) {
                targetOptions.add(MenuPrompt.MenuOption.enabled(
                    String.valueOf(idx), AnsiColors.colorResource(type)));
            }
            idx++;
        }

        var targetSelection = prompt.promptMenu(targetOptions, true, true);
        if (targetSelection.isBack()) return run(prompt);
        if (targetSelection.isQuit()) return true;

        int targetIndex = Integer.parseInt(targetSelection.choice()) - 1;
        ResourceType targetType = ResourceType.values()[targetIndex];

        // Confirm
        boolean confirmed = prompt.promptYesNo(
            "Confirm: give " + ratio + " " + sourceType.name().toLowerCase()
            + ", receive 1 " + targetType.name().toLowerCase() + "?");

        if (!confirmed) return false;

        // Execute
        var result = subAction.execute(player, sourceType, targetType, gameService.getGame());
        gameService.getEngine().markSubstituteUsed();

        if (result.success()) {
            log.info("{} substituted {} {}:{}", player.getName(), result.message(), sourceType, targetType);
            System.out.println(AnsiColors.green(result.message()));
        } else {
            System.out.println(AnsiColors.red(result.message()));
        }

        prompt.promptEnter("\nPress ENTER to continue...");
        return false;
    }
}
