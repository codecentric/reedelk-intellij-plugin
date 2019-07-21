package com.esb.plugin.editor.properties.widget.input.script.trie;

import com.esb.plugin.editor.properties.widget.input.script.Suggestion;
import com.esb.plugin.editor.properties.widget.input.script.SuggestionToken;
import com.esb.plugin.editor.properties.widget.input.script.SuggestionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class TrieTest {

    private Trie trie;

    @BeforeEach
    void setUp() {
        this.trie = new Trie();
        this.trie.insert(new SuggestionToken("Abc", SuggestionType.VARIABLE));
        this.trie.insert(new SuggestionToken("Aabc", SuggestionType.VARIABLE));
        this.trie.insert(new SuggestionToken("Aabbc", SuggestionType.PROPERTY));
        this.trie.insert(new SuggestionToken("Aabbcc", SuggestionType.PROPERTY));
        this.trie.insert(new SuggestionToken("Bcd", SuggestionType.VARIABLE));
        this.trie.insert(new SuggestionToken("Bbcd", SuggestionType.PROPERTY));
        this.trie.insert(new SuggestionToken("Bbccd", SuggestionType.PROPERTY));
        this.trie.insert(new SuggestionToken("Bbccdd", SuggestionType.VARIABLE));
    }

    @Test
    void shouldSearchReturnTrue() {
        // When
        boolean result = trie.search("Aabbc");

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void shouldSearchReturnFalseWhenNotEmpty() {
        // When
        boolean result = trie.search("ffgghh");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void shouldSearchReturnFalseWhenEmpty() {
        // Given
        Trie trie = new Trie();

        // When
        boolean result = trie.search("ffgghh");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void shouldSearchByPrefixReturnEmptyWhenEmpty() {
        // Given
        Trie trie = new Trie();

        // When
        Set<Suggestion> suggestions = trie.searchByPrefix("f");

        // Then
        assertThat(suggestions).isEmpty();
    }

    @Test
    void shouldSearchByPrefixReturnCorrectSuggestions1() {
        // When
        Set<Suggestion> suggestions = trie.searchByPrefix("A");

        // Then
        assertThat(suggestions).hasSize(4);
        assertThatExistsSuggestion(suggestions, "Abc", SuggestionType.VARIABLE);
        assertThatExistsSuggestion(suggestions, "Aabc", SuggestionType.VARIABLE);
        assertThatExistsSuggestion(suggestions, "Aabbc", SuggestionType.PROPERTY);
        assertThatExistsSuggestion(suggestions, "Aabbcc", SuggestionType.PROPERTY);
    }

    @Test
    void shouldSearchByPrefixReturnCorrectSuggestions2() {
        // When
        Set<Suggestion> suggestions = trie.searchByPrefix("Aabb");

        // Then
        assertThat(suggestions).hasSize(2);
        assertThatExistsSuggestion(suggestions, "Aabbc", SuggestionType.PROPERTY);
        assertThatExistsSuggestion(suggestions, "Aabbcc", SuggestionType.PROPERTY);
    }

    @Test
    void shouldSearchByPrefixReturnCorrectSuggestions3() {
        // When
        Set<Suggestion> suggestions = trie.searchByPrefix("Bbccd");

        // Then
        assertThat(suggestions).hasSize(2);
        assertThatExistsSuggestion(suggestions, "Bbccd", SuggestionType.PROPERTY);
        assertThatExistsSuggestion(suggestions, "Bbccdd", SuggestionType.VARIABLE);
    }

    private void assertThatExistsSuggestion(Set<Suggestion> suggestions, String expectedToken, SuggestionType expectedType) {
        for (Suggestion suggestion : suggestions) {
            if (expectedToken.equals(suggestion.getToken()) &&
                    expectedType.equals(suggestion.getSuggestionType())) {
                return;
            }
        }
        fail(format("Could not find suggestion matching token=%s and type=%s", expectedToken, expectedType));
    }
}