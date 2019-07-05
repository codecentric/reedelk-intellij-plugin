package com.esb.plugin.editor.properties.widget.input.script.trie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
        List<String> suggestions = trie.searchByPrefix("f");

        // Then
        assertThat(suggestions).isEmpty();
    }

    @Test
    void shouldSearchByPrefixReturnCorrectSuggestions1() {
        // When
        List<String> suggestions = trie.searchByPrefix("A");

        // Then
        assertThat(suggestions).containsExactlyInAnyOrder("Abc", "Aabc", "Aabbc", "Aabbcc");
    }

    @Test
    void shouldSearchByPrefixReturnCorrectSuggestions2() {
        // When
        List<String> suggestions = trie.searchByPrefix("Aabb");

        // Then
        assertThat(suggestions).containsExactlyInAnyOrder("Aabbc", "Aabbcc");
    }

    @Test
    void shouldSearchByPrefixReturnCorrectSuggestions3() {
        // When
        List<String> suggestions = trie.searchByPrefix("Bbccd");

        // Then
        assertThat(suggestions).containsExactlyInAnyOrder("Bbccd", "Bbccdd");
    }
}