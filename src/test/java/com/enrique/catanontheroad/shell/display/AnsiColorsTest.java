package com.enrique.catanontheroad.shell.display;

import com.enrique.catanontheroad.game.card.ResourceType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AnsiColorsTest {

    @Test
    void should_color_brick_red() {
        String result = AnsiColors.colorResource(ResourceType.BRICK);
        assertThat(result).contains("brick").contains(AnsiColors.RED).contains(AnsiColors.RESET);
    }

    @Test
    void should_color_wood_green() {
        String result = AnsiColors.colorResource(ResourceType.WOOD);
        assertThat(result).contains("wood").contains(AnsiColors.GREEN);
    }

    @Test
    void should_color_wool_white() {
        String result = AnsiColors.colorResource(ResourceType.WOOL);
        assertThat(result).contains("wool").contains(AnsiColors.WHITE);
    }

    @Test
    void should_color_wheat_yellow() {
        String result = AnsiColors.colorResource(ResourceType.WHEAT);
        assertThat(result).contains("wheat").contains(AnsiColors.YELLOW);
    }

    @Test
    void should_color_ore_cyan() {
        String result = AnsiColors.colorResource(ResourceType.ORE);
        assertThat(result).contains("ore").contains(AnsiColors.CYAN);
    }

    @Test
    void should_color_resource_by_string_name() {
        String result = AnsiColors.colorResource("brick");
        assertThat(result).contains("brick").contains(AnsiColors.RED);
    }

    @Test
    void should_return_unknown_type_unchanged() {
        String result = AnsiColors.colorResource("diamond");
        assertThat(result).isEqualTo("diamond");
    }

    @Test
    void should_make_text_bold() {
        String result = AnsiColors.bold("hello");
        assertThat(result).isEqualTo(AnsiColors.BOLD + "hello" + AnsiColors.RESET);
    }

    @Test
    void should_make_text_dim() {
        String result = AnsiColors.dim("faded");
        assertThat(result).isEqualTo(AnsiColors.DIM + "faded" + AnsiColors.RESET);
    }

    @Test
    void should_make_text_cyan() {
        String result = AnsiColors.cyan("prompt");
        assertThat(result).isEqualTo(AnsiColors.CYAN + "prompt" + AnsiColors.RESET);
    }

    @Test
    void should_make_text_red() {
        String result = AnsiColors.red("error");
        assertThat(result).isEqualTo(AnsiColors.RED + "error" + AnsiColors.RESET);
    }

    @Test
    void should_make_text_green() {
        String result = AnsiColors.green("success");
        assertThat(result).isEqualTo(AnsiColors.GREEN + "success" + AnsiColors.RESET);
    }

    @Test
    void should_make_text_bold_green() {
        String result = AnsiColors.boldGreen("winner");
        assertThat(result).isEqualTo(AnsiColors.BOLD + AnsiColors.GREEN + "winner" + AnsiColors.RESET);
    }
}
