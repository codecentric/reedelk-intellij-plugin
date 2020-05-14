package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.plugin.assertion.PluginAssertion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

class TrieDefaultTest {

    private TrieMapWrapper trieMapWrapper;

    @BeforeEach
    void setUp() {
        trieMapWrapper = new TrieMapWrapper();
    }

    @Test
    void shouldFindMultipleSuggestionsByPrefix() {
        // Given
        Suggestion suggestion1 = Suggestion.create(Suggestion.Type.PROPERTY)
                .lookup("property1")
                .returnType(String.class.getName())
                .returnTypeDisplayValue(String.class.getSimpleName())
                .build();

        Suggestion suggestion2 = Suggestion.create(Suggestion.Type.PROPERTY)
                .lookup("property2")
                .returnType(String.class.getName())
                .returnTypeDisplayValue(String.class.getSimpleName())
                .build();

        TrieDefault trie = new TrieDefault();
        trie.insert(suggestion1);
        trie.insert(suggestion2);

        Collection<Suggestion> suggestions = trie.autocomplete("", trieMapWrapper);
        PluginAssertion.assertThat(suggestions)
                .hasSize(2);
    }

    @Test
    void shouldAddMultipleSuggestionsWithDifferentSignature() {
        // Given
        Suggestion suggestion1 = Suggestion.create(Suggestion.Type.FUNCTION)
                .lookup("method1()")
                .lookupDisplayValue("method1")
                .tailText("()")
                .returnType(String.class.getName())
                .returnTypeDisplayValue(String.class.getSimpleName())
                .build();

        Suggestion suggestion2 = Suggestion.create(Suggestion.Type.FUNCTION)
                .lookup("method1()")
                .lookupDisplayValue("method1")
                .tailText("(String key)")
                .returnType(String.class.getName())
                .returnTypeDisplayValue(String.class.getSimpleName())
                .build();

        TrieDefault trie = new TrieDefault();
        trie.insert(suggestion1);
        trie.insert(suggestion2);

        Collection<Suggestion> suggestions = trie.autocomplete("", trieMapWrapper);
        PluginAssertion.assertThat(suggestions)
                .hasSize(2);
    }
/**
 // When
 List<Suggestion> suggestions = trie.autocomplete("me");

 // Then
 PluginAssertion.assertThat(suggestions)
 .hasSize(2)
 .contains(suggestion1)
 .contains(suggestion2);*/

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
