package com.enrique.catanontheroad.shell.input;

import com.enrique.catanontheroad.shell.display.AnsiColors;
import org.jline.reader.LineReader;

import java.util.List;

public class MenuPrompt {

    public record MenuOption(String key, String label, boolean enabled, String disabledReason) {
        public static MenuOption enabled(String key, String label) {
            return new MenuOption(key, label, true, null);
        }

        public static MenuOption disabled(String key, String label, String reason) {
            return new MenuOption(key, label, false, reason);
        }
    }

    public enum SpecialInput {
        BACK, QUIT
    }

    public record MenuSelection(String choice, SpecialInput special) {
        public static MenuSelection option(String choice) {
            return new MenuSelection(choice, null);
        }

        public static MenuSelection back() {
            return new MenuSelection(null, SpecialInput.BACK);
        }

        public static MenuSelection quit() {
            return new MenuSelection(null, SpecialInput.QUIT);
        }

        public boolean isBack() {
            return special == SpecialInput.BACK;
        }

        public boolean isQuit() {
            return special == SpecialInput.QUIT;
        }

        public boolean isOption() {
            return special == null;
        }
    }

    private final LineReader lineReader;

    public MenuPrompt(LineReader lineReader) {
        this.lineReader = lineReader;
    }

    public MenuSelection promptMenu(List<MenuOption> options, boolean showBack, boolean showQuit) {
        while (true) {
            for (MenuOption option : options) {
                if (option.enabled()) {
                    System.out.println("[" + option.key() + "] " + option.label());
                } else {
                    System.out.println(AnsiColors.dim(
                        "[" + option.key() + "] " + option.label() + " (" + option.disabledReason() + ")"
                    ));
                }
            }

            if (showBack) {
                System.out.println("[B] Back");
            }
            if (showQuit) {
                System.out.println("[Q] Quit & show final scores");
            }

            String input = readLine().trim().toUpperCase();

            if (showBack && input.equals("B")) {
                return MenuSelection.back();
            }
            if (showQuit && input.equals("Q")) {
                return MenuSelection.quit();
            }

            // Check if input matches an enabled option
            for (MenuOption option : options) {
                if (option.key().equalsIgnoreCase(input)) {
                    if (!option.enabled()) {
                        System.out.println(AnsiColors.red("That option is not available: " + option.disabledReason()));
                        System.out.println();
                        break;
                    }
                    return MenuSelection.option(input);
                }
            }

            // If we get here and didn't match anything, it's invalid
            boolean matched = options.stream().anyMatch(o -> o.key().equalsIgnoreCase(input));
            if (!matched) {
                System.out.println(AnsiColors.red("Invalid input. Please try again."));
                System.out.println();
            }
        }
    }

    public String promptText(String prompt, boolean showBack, boolean showQuit) {
        return promptText(prompt, showBack, showQuit, false);
    }

    public String promptText(String prompt, boolean showBack, boolean showQuit, boolean allowBlank) {
        while (true) {
            System.out.print(prompt);
            if (showBack) System.out.print("   [B] Back");
            if (showQuit) System.out.print("   [Q] Quit");
            System.out.println();

            String input = readLine().trim();

            if (showBack && input.equalsIgnoreCase("B")) {
                return "B";
            }
            if (showQuit && input.equalsIgnoreCase("Q")) {
                return "Q";
            }

            if (input.isEmpty() && !allowBlank) {
                System.out.println(AnsiColors.red("Input cannot be empty."));
                continue;
            }

            return input;
        }
    }

    public boolean promptYesNo(String prompt) {
        while (true) {
            System.out.println(prompt + " [Y/N]");
            String input = readLine().trim().toUpperCase();
            if (input.equals("Y")) return true;
            if (input.equals("N")) return false;
            System.out.println(AnsiColors.red("Please enter Y or N."));
        }
    }

    public void promptEnter(String message) {
        System.out.println(message);
        readLine();
    }

    public void clearScreen() {
        System.out.print(AnsiColors.CLEAR_SCREEN);
        System.out.flush();
    }

    public void showPassDevice(String playerName) {
        clearScreen();
        System.out.println("════════════════════════════════════════════════════════════");
        System.out.println("Pass the device to " + AnsiColors.bold(playerName) + ".");
        System.out.println();
        System.out.println("Press ENTER when " + playerName + " is ready...");
        System.out.println("════════════════════════════════════════════════════════════");
        readLine();
        clearScreen();
    }

    private String readLine() {
        return lineReader.readLine("");
    }
}
