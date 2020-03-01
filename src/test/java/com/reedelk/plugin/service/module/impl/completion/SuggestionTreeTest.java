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
        List<TrieResult> results = suggestionTree.autocomplete("message.");

        // Then
        assertThat(results).isEmpty();
    }

    @Test
    void shouldReturnCorrectTokenWhenRootMatches() {
        // Given
        SuggestionTree suggestionTree = new SuggestionTree(new HashMap<>());
        List<Suggestion> autocompleteItems = asList(utilSuggestion, configSuggestion);
        suggestionTree.add(autocompleteItems);

        // When
        List<TrieResult> results = suggestionTree.autocomplete("Ut");

        // Then
        assertThat(results).hasSize(1);

        TrieResult result = results.get(0);
        assertThat(result.getWord()).isEqualTo("Util");
        assertThat(result.getSuggestion()).isEqualTo(utilSuggestion);
    }

    @Test
    void shouldReturnAllRootVariablesWhenSuggestionTokenIsEmpty() {
        // Given
        SuggestionTree suggestionTree = new SuggestionTree(new HashMap<>());
        List<Suggestion> autocompleteItems = asList(utilSuggestion, configSuggestion);
        suggestionTree.add(autocompleteItems);

        // When
        List<TrieResult> results = suggestionTree.autocomplete("");

        // Then
        assertThat(results).hasSize(2);
        assertThatContains(results, "Util", utilSuggestion);
        assertThatContains(results, "Config", configSuggestion);
    }

    @Test
    void shouldReturnEmptyResultsWhenNoSuggestionsAndTokenIsEmpty() {
        // Given
        SuggestionTree suggestionTree = new SuggestionTree(new HashMap<>());

        // When
        List<TrieResult> results = suggestionTree.autocomplete("");

        // Then
        assertThat(results).isEmpty();
    }

    private void assertThatContains(List<TrieResult> results, String word, Suggestion suggestion) {
        boolean found = results.stream()
                .anyMatch(result -> word.equals(result.getWord()) && result.getSuggestion() == suggestion);
        assertThat(found).isTrue();
    }
}
