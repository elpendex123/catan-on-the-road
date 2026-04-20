package com.enrique.catanontheroad.shell.display;

import com.enrique.catanontheroad.game.card.ResourceType;

public final class AnsiColors {

    private AnsiColors() {}

    // Reset
    public static final String RESET = "\u001B[0m";

    // Regular colors
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String WHITE = "\u001B[97m"; // bright white
    public static final String CYAN = "\u001B[36m";
    public static final String DARK_GRAY = "\u001B[90m";

    // Styles
    public static final String BOLD = "\u001B[1m";
    public static final String DIM = "\u001B[2m";

    // Screen clear
    public static final String CLEAR_SCREEN = "\u001B[2J\u001B[H";

    public static String colorResource(ResourceType type) {
        return colorResource(type.name().toLowerCase());
    }

    public static String colorResource(String name) {
        return switch (name.toLowerCase()) {
            case "brick" -> RED + name + RESET;
            case "wood" -> GREEN + name + RESET;
            case "wool" -> WHITE + name + RESET;
            case "wheat" -> YELLOW + name + RESET;
            case "ore" -> CYAN + name + RESET;
            default -> name;
        };
    }

    public static String bold(String text) {
        return BOLD + text + RESET;
    }

    public static String dim(String text) {
        return DIM + text + RESET;
    }

    public static String cyan(String text) {
        return CYAN + text + RESET;
    }

    public static String red(String text) {
        return RED + text + RESET;
    }

    public static String green(String text) {
        return GREEN + text + RESET;
    }

    public static String boldGreen(String text) {
        return BOLD + GREEN + text + RESET;
    }
}
