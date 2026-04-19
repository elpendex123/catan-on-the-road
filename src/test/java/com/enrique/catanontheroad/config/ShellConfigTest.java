package com.enrique.catanontheroad.config;

import org.jline.utils.AttributedString;
import org.junit.jupiter.api.Test;
import org.springframework.shell.jline.PromptProvider;

import static org.assertj.core.api.Assertions.assertThat;

class ShellConfigTest {

    @Test
    void prompt_provider_should_return_catan_prompt() {
        ShellConfig config = new ShellConfig();
        PromptProvider promptProvider = config.promptProvider();

        AttributedString prompt = promptProvider.getPrompt();

        assertThat(prompt.toString()).isEqualTo("catan> ");
    }
}
