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

    @Test
    void shouldCorrectlyFindLastToken() {
        // Given
        String text = "function test(context,message) {\n" +
                "\tvar text = 'test function';\n" +
                "\tHttpPartBuilder.create().text('Mytext').\n" +
                "\treturn text + ' result';\n" +
                "}";

        int offset = 103;

        // When
        Optional<String> actual = provider.findLastToken(text, offset);

        // Then
        assertThat(actual).hasValue("HttpPartBuilder.create().text('Mytext').");
    }

    @Test
    void shouldCorrectlyFindLastTokenWhenArgumentTextWithEmptySpace() {
        // Given
        String text = "function test(context,message) {\n" +
                "\tvar text = 'test function';\n" +
                "\tHttpPartBuilder.create().text('My text').\n" +
                "\treturn text + ' result';\n" +
                "}";

        int offset = 104;

        // When
        Optional<String> actual = provider.findLastToken(text, offset);

        // Then
        assertThat(actual).hasValue("HttpPartBuilder.create().text('My text').");
    }

    @Test
    void shouldReturnEmptyWhenTextWithNotWellBalancedRoundParenthesis() {
        // Given
        String text = "Util.'My text').";

        int offset = 16;

        // When
        Optional<String> actual = provider.findLastToken(text, offset);

        // Then
        assertThat(actual).isNotPresent();
    }

    @Test
    void shouldReturnCorrectlyWhenTextInsideRoundParenthesis() {
        // Given
        String text = "Util.text('My text').";

        int offset = 21;

        // When
        Optional<String> actual = provider.findLastToken(text, offset);

        // Then
        assertThat(actual).contains("Util.text('My text').");
    }

    @Test
    void shouldReturnCorrectlyWhenTextInsideSquareBrackets() {
        // Given
        String text = "Util.text(['one text', 'two text']).";

        int offset = 36;

        // When
        Optional<String> actual = provider.findLastToken(text, offset);

        // Then
        assertThat(actual).contains("Util.text(['one text', 'two text']).");
    }
}
