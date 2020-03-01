package com.reedelk.plugin.service.module.impl.completion;

import com.reedelk.module.descriptor.model.AutocompleteItemDescriptor;
import com.reedelk.runtime.api.autocomplete.AutocompleteItemType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class SuggestionTreeTest {

    private AutocompleteItemDescriptor utilAutocompleteType = AutocompleteItemDescriptor.create()
            .global(true)
            .type("Util")
            .token("Util")
            .returnType("Util")
            .replaceValue("Util")
            .itemType(AutocompleteItemType.VARIABLE)
            .build();
    private Suggestion utilSuggestion = Suggestion.create(utilAutocompleteType);

    private AutocompleteItemDescriptor configAutocompleteType = AutocompleteItemDescriptor.create()
            .global(true)
            .type("Config")
            .token("Config")
            .returnType("Config")
            .replaceValue("Config")
            .itemType(AutocompleteItemType.VARIABLE)
            .build();
    private Suggestion configSuggestion = Suggestion.create(configAutocompleteType);

    @Test
    void shouldReturnEmptyWhenNoMatchesAreFound() {
        // Given
        SuggestionTree suggestionTree = new SuggestionTree(new HashMap<>());
        List<Suggestion> suggestions = asList(utilSuggestion, configSuggestion);
        suggestionTree.add(suggestions);

        // When
        List<Suggestion> actualSuggestions = suggestionTree.autocomplete("message.");

        // Then
        assertThat(actualSuggestions).isEmpty();
    }

    @Test
    void shouldReturnCorrectTokenWhenRootMatches() {
        // Given
        SuggestionTree suggestionTree = new SuggestionTree(new HashMap<>());
        List<Suggestion> autocompleteItems = asList(utilSuggestion, configSuggestion);
        suggestionTree.add(autocompleteItems);

        // When
        List<Suggestion> suggestions = suggestionTree.autocomplete("Ut");

        // Then
        assertThat(suggestions).hasSize(1);

        Suggestion suggestion = suggestions.get(0);
        assertThat(suggestion.getToken()).isEqualTo("Util");
        assertThat(suggestion).isEqualTo(utilSuggestion);
    }

    @Test
    void shouldReturnAllRootVariablesWhenSuggestionTokenIsEmpty() {
        // Given
        SuggestionTree suggestionTree = new SuggestionTree(new HashMap<>());
        List<Suggestion> autocompleteItems = asList(utilSuggestion, configSuggestion);
        suggestionTree.add(autocompleteItems);

        // When
        List<Suggestion> suggestions = suggestionTree.autocomplete("");

        // Then
        assertThat(suggestions).hasSize(2);
        assertThatContains(suggestions, "Util", utilSuggestion);
        assertThatContains(suggestions, "Config", configSuggestion);
    }

    @Test
    void shouldReturnEmptyResultsWhenNoSuggestionsAndTokenIsEmpty() {
        // Given
        SuggestionTree suggestionTree = new SuggestionTree(new HashMap<>());

        // When
        List<Suggestion> suggestions = suggestionTree.autocomplete("");

        // Then
        assertThat(suggestions).isEmpty();
    }

    private void assertThatContains(List<Suggestion> suggestions, String word, Suggestion expected) {
        boolean found = suggestions.stream()
                .anyMatch(suggestion -> word.equals(suggestion.getToken()) && suggestion == expected);
        assertThat(found).isTrue();
    }
}
