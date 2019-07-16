package com.esb.plugin.editor.properties.widget.input.script.trie;

import com.esb.plugin.editor.properties.widget.input.script.Suggestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class TrieTest {

    private Trie trie;

    @BeforeEach
    void setUp() {
        this.trie = new Trie();
        this.trie.insert("Abc");
        this.trie.insert("Aabc");
        this.trie.insert("Aabbc");
        this.trie.insert("Aabbcc");
        this.trie.insert("Bcd");
        this.trie.insert("Bbcd");
        this.trie.insert("Bbccd");
        this.trie.insert("Bbccdd");
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
        fail("Complete me");
        // TODO:
        //assertThat(suggestions).containsExactlyInAnyOrder("Abc", "Aabc", "Aabbc", "Aabbcc");
    }

    @Test
    void shouldSearchByPrefixReturnCorrectSuggestions2() {
        // When
        Set<Suggestion> suggestions = trie.searchByPrefix("Aabb");

        // Then
        fail("Complete me");
        // TODO:
        //assertThat(suggestions).containsExactlyInAnyOrder("Aabbc", "Aabbcc");
    }

    @Test
    void shouldSearchByPrefixReturnCorrectSuggestions3() {
        // When
        Set<Suggestion> suggestions = trie.searchByPrefix("Bbccd");

        // Then
        fail("Complete me");
        // TODO:
        //assertThat(suggestions).containsExactlyInAnyOrder("Bbccd", "Bbccdd");
    }
}