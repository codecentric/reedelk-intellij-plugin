package com.esb.plugin.editor.properties.widget.input.script.trie;

import com.esb.plugin.assertion.PluginAssertion;
import com.esb.plugin.editor.properties.widget.input.script.Suggestion;
import com.esb.plugin.editor.properties.widget.input.script.SuggestionToken;
import com.esb.plugin.editor.properties.widget.input.script.SuggestionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

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
        // Expect
        PluginAssertion.assertThat(trie).searchByPrefix("A")
                .hasNumberOfResults(4)
                .hasResult("Abc", SuggestionType.VARIABLE)
                .hasResult("Aabc", SuggestionType.VARIABLE)
                .hasResult("Aabbc", SuggestionType.PROPERTY)
                .hasResult("Aabbcc", SuggestionType.PROPERTY);
    }

    @Test
    void shouldSearchByPrefixReturnCorrectSuggestions2() {
        // Expect
        PluginAssertion.assertThat(trie).searchByPrefix("Aabb")
                .hasNumberOfResults(2)
                .hasResult("Aabbc", SuggestionType.PROPERTY)
                .hasResult("Aabbcc", SuggestionType.PROPERTY);
    }

    @Test
    void shouldSearchByPrefixReturnCorrectSuggestions3() {
        // Expect
        PluginAssertion.assertThat(trie).searchByPrefix("Bbccd")
                .hasNumberOfResults(2)
                .hasResult("Bbccd", SuggestionType.PROPERTY)
                .hasResult("Bbccdd", SuggestionType.VARIABLE);
    }
}