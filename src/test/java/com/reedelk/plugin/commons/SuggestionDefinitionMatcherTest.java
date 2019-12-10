package com.reedelk.plugin.commons;

import com.reedelk.plugin.service.module.impl.completion.SuggestionType;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SuggestionDefinitionMatcherTest {

    @Test
    void shouldCorrectlyParseSuggestionWithTypeAndTypeName() {
        // Given
        String suggestionTokenDefinition = "message[FUNCTION:Message]";

        // When
        Optional<Triple<String, SuggestionType, String>> parsed = SuggestionDefinitionMatcher.of(suggestionTokenDefinition);

        // Then
        assertThat(parsed).isPresent();

        Triple<String, SuggestionType, String> parsedSuggestion = parsed.get();
        assertThat(parsedSuggestion.getLeft()).isEqualTo("message");
        assertThat(parsedSuggestion.getMiddle()).isEqualTo(SuggestionType.FUNCTION);
        assertThat(parsedSuggestion.getRight()).isEqualTo("Message");
    }

    @Test
    void shouldCorrectlyParseSuggestionWithTypeAndTypeNameArray() {
        // Given
        String suggestionTokenDefinition = "message[VARIABLE:Message[]]";

        // When
        Optional<Triple<String, SuggestionType, String>> parsed = SuggestionDefinitionMatcher.of(suggestionTokenDefinition);

        // Then
        assertThat(parsed).isPresent();

        Triple<String, SuggestionType, String> parsedSuggestion = parsed.get();
        assertThat(parsedSuggestion.getLeft()).isEqualTo("message");
        assertThat(parsedSuggestion.getMiddle()).isEqualTo(SuggestionType.VARIABLE);
        assertThat(parsedSuggestion.getRight()).isEqualTo("Message[]");
    }

    @Test
    void shouldReturnEmptyWhenSuggestionIsNotWellFormed() {
        // Given
        String suggestionTokenDefinition = "message[]";

        // When
        Optional<Triple<String, SuggestionType, String>> parsed = SuggestionDefinitionMatcher.of(suggestionTokenDefinition);

        // Then
        assertThat(parsed).isNotPresent();
    }
}