package com.reedelk.plugin.service.module.impl.completion;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InputTokenizerTest {

    @Test
    void shouldCorrectlyTokenize() {
        // Given
        String input = "MultipartBuilder.part('image').binary(message.payload());";

        // When
        String[] result = InputTokenizer.tokenize(input);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).contains("MultipartBuilder");
        assertThat(result).contains("part");
        assertThat(result).contains("binary");
    }
}
