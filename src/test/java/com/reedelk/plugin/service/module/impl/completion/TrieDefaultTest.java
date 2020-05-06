package com.reedelk.plugin.service.module.impl.completion;

import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.service.module.impl.component.completion.Suggestion;
import com.reedelk.plugin.service.module.impl.component.completion.TrieDefault;
import org.junit.jupiter.api.Test;

import java.util.List;

class TrieDefaultTest {
// TODO: Finish me.
    @Test
    void shouldFindMultipleSuggestionsByPrefix() {
        // Given
        Suggestion suggestion1 = Suggestion.create(Suggestion.Type.PROPERTY)
                .withLookupString("method1")
                .withPresentableText("method1()")
                .withType(String.class.getName())
                .build();

        Suggestion suggestion2 = Suggestion.create(Suggestion.Type.PROPERTY)
                .withLookupString("method2")
                .withPresentableText("method2()")
                .withType(String.class.getName())
                .build();

        TrieDefault trie = new TrieDefault();
        trie.insert(suggestion1);
        trie.insert(suggestion2);

        // When
        List<Suggestion> suggestions = trie.autocomplete("me", );

        // Then
        PluginAssertion.assertThat(suggestions)
                .hasSize(2)
                .contains(suggestion1)
                .contains(suggestion2);
    }
/**
 *     @Test
 *     void shouldCorrectlyInsertSuggestionToken() {
 *         // Given
 *         Trie trie = new Trie();
 *
 *         // When
 *         trie.insert(SampleSuggestions.UTIL);
 *
 *         // Then
 *         List<Suggestion> result = trie.autocomplete("Ut");
 *         PluginAssertion.assertThat(result)
 *                 .containsOnly(SampleSuggestions.UTIL);
 *     }
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
    }*/
}
