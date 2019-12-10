package com.reedelk.plugin.service.module.impl.completion;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TrieTest {

    private final String suggestionToken1 = "message[VARIABLE:Message]";
    private final String suggestionToken2 = "multiply()[FUNCTION:int]";
    private final String suggestionToken3 = "message.mimeType1()[FUNCTION:MimeType]";
    private final String suggestionToken4 = "message.mimeType2()[FUNCTION:MimeType]";
    private final String suggestionToken5 = "message.mimeType3()[FUNCTION:MimeType]";

    @Test
    void shouldCorrectlyInsertSuggestionToken() {
        // Given
        Trie trie = new Trie();

        // When
        trie.insert(suggestionToken1);

        // Then
        Suggestion expected = Suggestion.from("message", SuggestionType.VARIABLE, "Message");

        List<Suggestion> results = trie.findByPrefix("m");
        assertThat(results).containsExactly(expected);
    }

    @Test
    void shouldCorrectlyFindMultipleSuggestionsByPrefix() {
        // Given
        Trie trie = new Trie();

        // When
        trie.insert(suggestionToken1);
        trie.insert(suggestionToken2);

        // Then
        Suggestion expected1 = Suggestion.from("message", SuggestionType.VARIABLE, "Message");
        Suggestion expected2 = Suggestion.from("multiply()", SuggestionType.FUNCTION, "int");

        List<Suggestion> results = trie.findByPrefix("m");
        assertThat(results).containsExactlyInAnyOrder(expected1, expected2);
    }

    @Test
    void shouldCorrectlyFindMultipleSuggestionsByPrefixAfterDot() {
        // Given
        Trie trie = new Trie();

        // When
        trie.insert(suggestionToken1);
        trie.insert(suggestionToken2);
        trie.insert(suggestionToken3);
        trie.insert(suggestionToken4);
        trie.insert(suggestionToken5);

        // Then
        Suggestion expected1 = Suggestion.from("mimeType1()", SuggestionType.FUNCTION, "MimeType");
        Suggestion expected2 = Suggestion.from("mimeType2()", SuggestionType.FUNCTION, "MimeType");
        Suggestion expected3 = Suggestion.from("mimeType3()", SuggestionType.FUNCTION, "MimeType");

        List<Suggestion> results = trie.findByPrefix("message.mim");
        assertThat(results).containsExactlyInAnyOrder(expected1, expected2, expected3);
    }

    @Test
    void shouldCorrectlyFindMultipleSuggestionsByPrefixWhenSearchPrefixEndsWithDot() {
        // Given
        Trie trie = new Trie();

        // When
        trie.insert(suggestionToken1);
        trie.insert(suggestionToken2);
        trie.insert(suggestionToken3);
        trie.insert(suggestionToken4);
        trie.insert(suggestionToken5);

        // Then
        Suggestion expected1 = Suggestion.from("mimeType1()", SuggestionType.FUNCTION, "MimeType");
        Suggestion expected2 = Suggestion.from("mimeType2()", SuggestionType.FUNCTION, "MimeType");
        Suggestion expected3 = Suggestion.from("mimeType3()", SuggestionType.FUNCTION, "MimeType");

        List<Suggestion> results = trie.findByPrefix("message.");
        assertThat(results).containsExactlyInAnyOrder(expected1, expected2, expected3);
    }

    @Test
    void shouldCorrectlyFindMultipleSuggestionsByPrefixWhenSearchPrefixEndsWithFunction() {
        // Given
        Trie trie = new Trie();

        // When
        trie.insert(suggestionToken1);
        trie.insert(suggestionToken2);
        trie.insert(suggestionToken3);
        trie.insert(suggestionToken4);
        trie.insert(suggestionToken5);

        // Then
        List<Suggestion> results = trie.findByPrefix("message.mimeType1()");
        assertThat(results).isEmpty();
    }
}