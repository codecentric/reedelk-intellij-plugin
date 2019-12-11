package com.reedelk.plugin.commons;

import com.reedelk.plugin.service.module.impl.completion.Suggestion;
import com.reedelk.plugin.service.module.impl.completion.SuggestionType;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SuggestionDefinitionMatcherTest {

    @Test
    void shouldCorrectlyParseSuggestionWithTypeAndTypeName() {
        // Given
        String suggestionTokenDefinition = "message[FUNCTION:Message]";

        // When
        Optional<Suggestion> parsed = SuggestionDefinitionMatcher.of(suggestionTokenDefinition);

        // Then
        assertThat(parsed).isPresent();

        Suggestion parsedSuggestion = parsed.get();
        assertThat(parsedSuggestion.getToken()).isEqualTo("message");
        assertThat(parsedSuggestion.getType()).isEqualTo(SuggestionType.FUNCTION);
        assertThat(parsedSuggestion.getTypeName()).isEqualTo("Message");
        assertThat(parsedSuggestion.getOffset()).isNotPresent();
    }

    @Test
    void shouldCorrectlyParseSuggestionWithOffset() {
        // Given
        String suggestionTokenDefinition = "message[FUNCTION:Message:2]";

        // When
        Optional<Suggestion> parsed = SuggestionDefinitionMatcher.of(suggestionTokenDefinition);

        // Then
        assertThat(parsed).isPresent();

        Suggestion parsedSuggestion = parsed.get();
        assertThat(parsedSuggestion.getToken()).isEqualTo("message");
        assertThat(parsedSuggestion.getType()).isEqualTo(SuggestionType.FUNCTION);
        assertThat(parsedSuggestion.getTypeName()).isEqualTo("Message");
        assertThat(parsedSuggestion.getOffset()).isPresent();
        assertThat(parsedSuggestion.getOffset()).hasValue(2);
    }

    @Test
    void shouldCorrectlyParseSuggestionWithNotANumericOffset() {
        // Given
        String suggestionTokenDefinition = "message[FUNCTION:Message:2Hello]";

        // When
        Optional<Suggestion> parsed = SuggestionDefinitionMatcher.of(suggestionTokenDefinition);

        // Then
        assertThat(parsed).isPresent();

        Suggestion parsedSuggestion = parsed.get();
        assertThat(parsedSuggestion.getToken()).isEqualTo("message");
        assertThat(parsedSuggestion.getType()).isEqualTo(SuggestionType.FUNCTION);
        assertThat(parsedSuggestion.getTypeName()).isEqualTo("Message");
        assertThat(parsedSuggestion.getOffset()).isNotPresent();
    }

    @Test
    void shouldCorrectlyParseSuggestionWithTypeAndTypeNameArray() {
        // Given
        String suggestionTokenDefinition = "message[VARIABLE:Message[]]";

        // When
        Optional<Suggestion> parsed = SuggestionDefinitionMatcher.of(suggestionTokenDefinition);

        // Then
        assertThat(parsed).isPresent();

        Suggestion parsedSuggestion = parsed.get();
        assertThat(parsedSuggestion.getToken()).isEqualTo("message");
        assertThat(parsedSuggestion.getType()).isEqualTo(SuggestionType.VARIABLE);
        assertThat(parsedSuggestion.getTypeName()).isEqualTo("Message[]");
        assertThat(parsedSuggestion.getOffset()).isNotPresent();
    }

    @Test
    void shouldReturnEmptyWhenSuggestionIsNotWellFormed() {
        // Given
        String suggestionTokenDefinition = "message[]";

        // When
        Optional<Suggestion> parsed = SuggestionDefinitionMatcher.of(suggestionTokenDefinition);

        // Then
        assertThat(parsed).isNotPresent();
    }

    @Test
    void shouldCorrectlyParseSuggestionWithPutItem() {
        // Given
        String suggestionTokenDefinition = "context.put('',item)[FUNCTION:void:7]";

        // When
        Optional<Suggestion> parsed = SuggestionDefinitionMatcher.of(suggestionTokenDefinition);

        // Then
        assertThat(parsed).isPresent();

        Suggestion parsedSuggestion = parsed.get();
        assertThat(parsedSuggestion.getToken()).isEqualTo("context.put('',item)");
        assertThat(parsedSuggestion.getType()).isEqualTo(SuggestionType.FUNCTION);
        assertThat(parsedSuggestion.getTypeName()).isEqualTo("void");
        assertThat(parsedSuggestion.getOffset()).hasValue(7);
    }
}