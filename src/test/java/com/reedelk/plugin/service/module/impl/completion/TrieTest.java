package com.reedelk.plugin.service.module.impl.completion;

import com.reedelk.module.descriptor.model.AutocompleteItemDescriptor;
import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.runtime.api.autocomplete.AutocompleteType;
import org.junit.jupiter.api.Test;

import java.util.List;

class TrieTest {

    @Test
    void shouldCorrectlyInsertSuggestionToken() {
        // Given
        Trie trie = new Trie();

        // When
        trie.insert(SampleSuggestions.UTIL);

        // Then
        List<Suggestion> result = trie.autocomplete("Ut");
        PluginAssertion.assertThat(result)
                .containsOnly(SampleSuggestions.UTIL);
    }

    @Test
    void shouldCorrectlyFindMultipleSuggestionsByPrefix() {
        // Given
        AutocompleteItemDescriptor item1 = AutocompleteItemDescriptor.create()
                .type("MyType")
                .token("text")
                .returnType("MyType")
                .replaceValue("text()")
                .itemType(AutocompleteType.VARIABLE)
                .build();
        Suggestion suggestion1 = Suggestion.create(item1);

        AutocompleteItemDescriptor item2 = AutocompleteItemDescriptor.create()
                .type("MyType")
                .token("textWithMimeType")
                .returnType("MyType")
                .replaceValue("textWithMimeType()")
                .itemType(AutocompleteType.VARIABLE)
                .build();
        Suggestion suggestion2 = Suggestion.create(item2);

        Trie trie = new Trie();

        trie.insert(suggestion1);
        trie.insert(suggestion2);

        // When
        List<Suggestion> suggestions = trie.autocomplete("te");

        // Then

        PluginAssertion.assertThat(suggestions)
                .hasSize(2)
                .contains(suggestion1)
                .contains(suggestion2);
    }

    @Test
    void shouldCorrectlyAllSuggestionsWhenEmptyString() {
        // Given
        Trie trie = new Trie();
        trie.insert(SampleSuggestions.UTIL);
        trie.insert(SampleSuggestions.CONFIG);

        // When
        List<Suggestion> suggestions = trie.autocomplete("");

        // Then
        PluginAssertion.assertThat(suggestions)
                .hasSize(2)
                .contains(SampleSuggestions.UTIL)
                .contains(SampleSuggestions.CONFIG);
    }

    @Test
    void shouldReturnEmptySuggestions() {
        // Given
        Trie trie = new Trie();
        trie.insert(SampleSuggestions.UTIL);
        trie.insert(SampleSuggestions.CONFIG);

        // When
        List<Suggestion> suggestions = trie.autocomplete("Log");

        // Then
        PluginAssertion.assertThat(suggestions)
                .isEmpty();
    }
}
