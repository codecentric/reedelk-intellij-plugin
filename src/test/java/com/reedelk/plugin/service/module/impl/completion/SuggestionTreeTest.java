package com.reedelk.plugin.service.module.impl.completion;

import com.reedelk.module.descriptor.model.AutocompleteItemDescriptor;
import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.runtime.api.autocomplete.AutocompleteItemType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static java.util.Arrays.asList;

class SuggestionTreeTest {

    @Test
    void shouldReturnEmptyWhenNoMatchesAreFound() {
        // Given
        SuggestionTree suggestionTree = new SuggestionTree(new HashMap<>());
        List<Suggestion> suggestions = asList(SampleSuggestions.UTIL, SampleSuggestions.CONFIG);
        suggestionTree.add(suggestions);

        // When
        List<Suggestion> actualSuggestions = suggestionTree.autocomplete("message.");

        // Then
        PluginAssertion.assertThat(actualSuggestions)
                .isEmpty();
    }

    @Test
    void shouldReturnCorrectTokenWhenRootMatches() {
        // Given
        SuggestionTree suggestionTree = new SuggestionTree(new HashMap<>());
        List<Suggestion> autocompleteItems = asList(SampleSuggestions.UTIL, SampleSuggestions.CONFIG);
        suggestionTree.add(autocompleteItems);

        // When
        List<Suggestion> suggestions = suggestionTree.autocomplete("Ut");

        // Then
        PluginAssertion.assertThat(suggestions)
                .hasSize(1)
                .contains(SampleSuggestions.UTIL);
    }

    @Test
    void shouldReturnAllRootVariablesWhenSuggestionTokenIsEmpty() {
        // Given
        SuggestionTree suggestionTree = new SuggestionTree(new HashMap<>());
        List<Suggestion> autocompleteItems = asList(SampleSuggestions.UTIL, SampleSuggestions.CONFIG);
        suggestionTree.add(autocompleteItems);

        // When
        List<Suggestion> suggestions = suggestionTree.autocomplete("");

        // Then
        PluginAssertion.assertThat(suggestions)
                .hasSize(2)
                .contains(SampleSuggestions.UTIL)
                .contains(SampleSuggestions.CONFIG);
    }

    @Test
    void shouldReturnEmptyResultsWhenNoSuggestionsAndTokenIsEmpty() {
        // Given
        SuggestionTree suggestionTree = new SuggestionTree(new HashMap<>());

        // When
        List<Suggestion> suggestions = suggestionTree.autocomplete("");

        // Then
        PluginAssertion.assertThat(suggestions)
                .isEmpty();
    }

    @Test
    void shouldCorrectlySuggest() {
        // Given
        AutocompleteItemDescriptor tmpDirItem = AutocompleteItemDescriptor.create()
                .type("Util")
                .token("tmpdir")
                .returnType("String")
                .replaceValue("tmpdir()")
                .itemType(AutocompleteItemType.FUNCTION)
                .build();
        Suggestion tmpDir = Suggestion.create(tmpDirItem);

        SuggestionTree suggestionTree = new SuggestionTree(new HashMap<>());
        List<Suggestion> autocompleteItems = asList(SampleSuggestions.UTIL, tmpDir);
        suggestionTree.add(autocompleteItems);

        // When
        List<Suggestion> autocomplete = suggestionTree.autocomplete("U.tm");

        // Then
        PluginAssertion.assertThat(autocomplete).isEmpty();
    }
}
