package com.reedelk.plugin.service.module.impl.completion;

import com.reedelk.module.descriptor.model.AutocompleteItemDescriptor;
import com.reedelk.runtime.api.autocomplete.AutocompleteItemType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TrieTest {

    @Test
    void shouldCorrectlyInsertSuggestionToken() {
        // Given
        AutocompleteItemDescriptor itemDescriptor = AutocompleteItemDescriptor.create()
                .global(true)
                .token("Util")
                .type("Util")
                .cursorOffset(2)
                .returnType("Util")
                .replaceValue("Util")
                .description("Script utilities")
                .itemType(AutocompleteItemType.VARIABLE)
                .build();

        Suggestion suggestion = Suggestion.create(itemDescriptor);
        Trie trie = new Trie();

        // When
        trie.insert(suggestion);

        // Then
        List<Suggestion> result = trie.autocomplete("Ut");
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(suggestion);
    }

    /**

    @Test
    void shouldCorrectlyFindMultipleSuggestionsByPrefix() {
        // Given
        Trie trie = new Trie();

        // When
        trie.insert(asList(suggestionToken1, suggestionToken2));

        // Then
        List<Suggestion> results = trie.findByPrefix("m");
        PluginAssertion.assertThat(results)
                .contains("message", SuggestionType.VARIABLE, "Message")
                .contains("multiply()", SuggestionType.FUNCTION, "int")
                .hasSize(2);
    }

    @Test
    void shouldCorrectlyFindMultipleSuggestionsByPrefixAfterDot() {
        // Given
        Trie trie = new Trie();

        // When
        trie.insert(suggestionToken1, suggestionToken2, suggestionToken3, suggestionToken4, suggestionToken5);

        // Then
        List<Suggestion> results = trie.findByPrefix("message.mim");
        PluginAssertion.assertThat(results)
                .contains("mimeType1()", SuggestionType.FUNCTION, "MimeType")
                .contains("mimeType2()", SuggestionType.FUNCTION, "MimeType")
                .contains("mimeType3()", SuggestionType.FUNCTION, "MimeType")
                .hasSize(3);
    }

    @Test
    void shouldCorrectlyFindMultipleSuggestionsByPrefixWhenSearchPrefixEndsWithDot() {
        // Given
        Trie trie = new Trie();

        // When
        trie.insert(suggestionToken1, suggestionToken2, suggestionToken3, suggestionToken4, suggestionToken5);

        // Then
        List<Suggestion> results = trie.findByPrefix("message.");
        PluginAssertion.assertThat(results)
                .contains("mimeType1()", SuggestionType.FUNCTION, "MimeType")
                .contains("mimeType2()", SuggestionType.FUNCTION, "MimeType")
                .contains("mimeType3()", SuggestionType.FUNCTION, "MimeType")
                .hasSize(3);
    }

    @Test
    void shouldCorrectlyFindMultipleSuggestionsByPrefixWhenSearchPrefixEndsWithFunction() {
        // Given
        Trie trie = new Trie();

        // When
        trie.insert(suggestionToken1, suggestionToken2, suggestionToken3, suggestionToken4, suggestionToken5);

        // Then
        List<Suggestion> results = trie.findByPrefix("message.mimeType1()");
        PluginAssertion.assertThat(results).isEmpty();
    }

    @Test
    void shouldCorrectFindAllRootSuggestionsWhenPrefixIsEmptyString() {
        // Given
        Trie trie = new Trie();

        // When
        trie.insert(asList(suggestionToken1, suggestionToken2, suggestionToken3, suggestionToken4, suggestionToken5));

        // Then
        List<Suggestion> results = trie.findByPrefix("");

        PluginAssertion.assertThat(results)
                .contains("message", SuggestionType.VARIABLE, "Message")
                .contains("multiply()", SuggestionType.FUNCTION, "int")
                .hasSize(2);
    }

    @Test
    void shouldReturnEmptyWhenPrefixEndsWithOpenParenthesis() {
        // Given
        Trie trie = new Trie();

        // When
        trie.insert(suggestionToken1, suggestionToken2, suggestionToken3, suggestionToken4, suggestionToken5);

        // Then
        List<Suggestion> results = trie.findByPrefix("message.mimeType1(");
        PluginAssertion.assertThat(results).isEmpty();
    }

    @Test
    void shouldCorrectlyReturnPut() {
        // Given
        Trie trie = new Trie();

        // When
        trie.insert(suggestionToken6);

        // Then
        List<Suggestion> results = trie.findByPrefix("message.pu");
        PluginAssertion.assertThat(results).hasSize(1);
    }*/
}
