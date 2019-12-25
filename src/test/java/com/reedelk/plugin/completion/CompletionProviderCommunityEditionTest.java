package com.reedelk.plugin.completion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CompletionProviderCommunityEditionTest {

    private CompletionProviderCommunityEdition provider;

    @BeforeEach
    void setUp() {
        provider = new CompletionProviderCommunityEdition();
    }

    @Test
    void shouldCorrectlyFindLastTokenStartingWithTab() {
        // Given
        String text = "\tmess";

        // When
        Optional<String> actual = provider.findLastToken(text, 5);

        //  Then
        assertThat(actual).hasValue("mess");
    }

    @Test
    void shouldCorrectlyFindLastTokenStartingWithWhiteSpaces() {
        // Given
        String text = "  messa";

        // When
        Optional<String> actual = provider.findLastToken(text, 7);

        //  Then
        assertThat(actual).hasValue("messa");
    }

    @Test
    void shouldCorrectlyFindLastTokenStartingWithWhiteNewLine() {
        // Given
        String text = "\nmessa";

        // When
        Optional<String> actual = provider.findLastToken(text, 6);

        //  Then
        assertThat(actual).hasValue("messa");
    }

    @Test
    void shouldCorrectlyFindLastTokenStartingWithWhiteNewLineAndTab() {
        // Given
        String text = "\n\tmessa";

        // When
        Optional<String> actual = provider.findLastToken(text, 7);

        //  Then
        assertThat(actual).hasValue("messa");
    }
}