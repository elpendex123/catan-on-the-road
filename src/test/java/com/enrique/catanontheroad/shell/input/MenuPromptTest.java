package com.enrique.catanontheroad.shell.input;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MenuPromptTest {

    private PrintStream originalOut;
    private ByteArrayOutputStream capturedOut;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        capturedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOut));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    private MenuPrompt createPrompt(String... inputs) {
        return new MenuPrompt(StubLineReader.create(inputs));
    }

    @Test
    void should_return_selected_option() {
        MenuPrompt prompt = createPrompt("1");
        var options = List.of(MenuPrompt.MenuOption.enabled("1", "Option one"));

        var selection = prompt.promptMenu(options, false, false);

        assertThat(selection.isOption()).isTrue();
        assertThat(selection.choice()).isEqualTo("1");
    }

    @Test
    void should_return_back() {
        MenuPrompt prompt = createPrompt("b");
        var options = List.of(MenuPrompt.MenuOption.enabled("1", "Option"));

        var selection = prompt.promptMenu(options, true, false);

        assertThat(selection.isBack()).isTrue();
    }

    @Test
    void should_return_quit() {
        MenuPrompt prompt = createPrompt("q");
        var options = List.of(MenuPrompt.MenuOption.enabled("1", "Option"));

        var selection = prompt.promptMenu(options, false, true);

        assertThat(selection.isQuit()).isTrue();
    }

    @Test
    void should_reject_disabled_option_then_accept_valid() {
        MenuPrompt prompt = createPrompt("1", "2");
        var options = List.of(
            MenuPrompt.MenuOption.disabled("1", "Disabled", "already used"),
            MenuPrompt.MenuOption.enabled("2", "Enabled")
        );

        var selection = prompt.promptMenu(options, false, false);

        assertThat(selection.choice()).isEqualTo("2");
        assertThat(capturedOut.toString()).contains("not available");
    }

    @Test
    void should_reject_invalid_input_then_accept_valid() {
        MenuPrompt prompt = createPrompt("X", "1");
        var options = List.of(MenuPrompt.MenuOption.enabled("1", "Option"));

        var selection = prompt.promptMenu(options, false, false);

        assertThat(selection.choice()).isEqualTo("1");
        assertThat(capturedOut.toString()).contains("Invalid input");
    }

    @Test
    void should_display_disabled_options_with_reason() {
        MenuPrompt prompt = createPrompt("Q");
        var options = List.of(
            MenuPrompt.MenuOption.disabled("1", "Trade", "already used this turn")
        );

        prompt.promptMenu(options, false, true);

        assertThat(capturedOut.toString()).contains("already used this turn");
    }

    @Test
    void prompt_text_should_return_input() {
        MenuPrompt prompt = createPrompt("hello");

        String result = prompt.promptText("Enter:", false, false);

        assertThat(result).isEqualTo("hello");
    }

    @Test
    void prompt_text_should_return_back() {
        MenuPrompt prompt = createPrompt("B");

        String result = prompt.promptText("Enter:", true, false);

        assertThat(result).isEqualTo("B");
    }

    @Test
    void prompt_text_should_return_quit() {
        MenuPrompt prompt = createPrompt("Q");

        String result = prompt.promptText("Enter:", false, true);

        assertThat(result).isEqualTo("Q");
    }

    @Test
    void prompt_text_should_reject_empty_then_accept() {
        MenuPrompt prompt = createPrompt("", "valid");

        String result = prompt.promptText("Enter:", false, false);

        assertThat(result).isEqualTo("valid");
        assertThat(capturedOut.toString()).contains("empty");
    }

    @Test
    void prompt_yes_no_should_return_true_for_y() {
        MenuPrompt prompt = createPrompt("Y");

        assertThat(prompt.promptYesNo("Confirm?")).isTrue();
    }

    @Test
    void prompt_yes_no_should_return_false_for_n() {
        MenuPrompt prompt = createPrompt("N");

        assertThat(prompt.promptYesNo("Confirm?")).isFalse();
    }

    @Test
    void prompt_yes_no_should_reject_invalid_then_accept() {
        MenuPrompt prompt = createPrompt("X", "Y");

        assertThat(prompt.promptYesNo("Confirm?")).isTrue();
        assertThat(capturedOut.toString()).contains("Y or N");
    }

    @Test
    void prompt_enter_should_display_message() {
        MenuPrompt prompt = createPrompt("");

        prompt.promptEnter("Press ENTER");

        assertThat(capturedOut.toString()).contains("Press ENTER");
    }

    @Test
    void clear_screen_should_output_ansi_escape() {
        MenuPrompt prompt = createPrompt();

        prompt.clearScreen();

        assertThat(capturedOut.toString()).contains("\u001B[2J\u001B[H");
    }

    @Test
    void show_pass_device_should_display_player_name() {
        MenuPrompt prompt = createPrompt("", "");

        prompt.showPassDevice("Bob");

        assertThat(capturedOut.toString()).contains("Bob");
        assertThat(capturedOut.toString()).contains("Pass the device");
    }

    @Test
    void menu_selection_option_should_have_correct_state() {
        var sel = MenuPrompt.MenuSelection.option("1");
        assertThat(sel.isOption()).isTrue();
        assertThat(sel.isBack()).isFalse();
        assertThat(sel.isQuit()).isFalse();
        assertThat(sel.choice()).isEqualTo("1");
    }

    @Test
    void menu_selection_back_should_have_correct_state() {
        var sel = MenuPrompt.MenuSelection.back();
        assertThat(sel.isBack()).isTrue();
        assertThat(sel.isOption()).isFalse();
        assertThat(sel.isQuit()).isFalse();
    }

    @Test
    void menu_selection_quit_should_have_correct_state() {
        var sel = MenuPrompt.MenuSelection.quit();
        assertThat(sel.isQuit()).isTrue();
        assertThat(sel.isOption()).isFalse();
        assertThat(sel.isBack()).isFalse();
    }

    @Test
    void menu_option_enabled_should_have_correct_state() {
        var opt = MenuPrompt.MenuOption.enabled("1", "Test");
        assertThat(opt.enabled()).isTrue();
        assertThat(opt.key()).isEqualTo("1");
        assertThat(opt.label()).isEqualTo("Test");
        assertThat(opt.disabledReason()).isNull();
    }

    @Test
    void menu_option_disabled_should_have_correct_state() {
        var opt = MenuPrompt.MenuOption.disabled("1", "Test", "reason");
        assertThat(opt.enabled()).isFalse();
        assertThat(opt.disabledReason()).isEqualTo("reason");
    }

    @Test
    void should_handle_case_insensitive_back() {
        MenuPrompt prompt = createPrompt("B");
        var options = List.of(MenuPrompt.MenuOption.enabled("1", "Option"));

        var selection = prompt.promptMenu(options, true, false);

        assertThat(selection.isBack()).isTrue();
    }

    @Test
    void should_handle_case_insensitive_quit() {
        MenuPrompt prompt = createPrompt("Q");
        var options = List.of(MenuPrompt.MenuOption.enabled("1", "Option"));

        var selection = prompt.promptMenu(options, false, true);

        assertThat(selection.isQuit()).isTrue();
    }

    @Test
    void special_input_enum_values_should_exist() {
        assertThat(MenuPrompt.SpecialInput.values()).hasSize(2);
        assertThat(MenuPrompt.SpecialInput.valueOf("BACK")).isEqualTo(MenuPrompt.SpecialInput.BACK);
        assertThat(MenuPrompt.SpecialInput.valueOf("QUIT")).isEqualTo(MenuPrompt.SpecialInput.QUIT);
    }
}
