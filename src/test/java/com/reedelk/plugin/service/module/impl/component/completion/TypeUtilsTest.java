package com.reedelk.plugin.service.module.impl.component.completion;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TypeUtilsTest {

    @Test
    void shouldCorrectlyConvertToSimpleName() {
        // Given
        String input = String.class.getName();

        // When
        String actual = TypeUtils.toSimpleName(input);

        // Then
        assertThat(actual).isEqualTo("String");
    }

    @Test
    void shouldKeepSimpleName() {
        // Given
        String input = "MyType";

        // When
        String actual = TypeUtils.toSimpleName(input);

        // Then
        assertThat(actual).isEqualTo("MyType");
    }

    @Test
    void shouldReturnEmptySimpleName() {
        // Given
        String input = null;

        // When
        String actual = TypeUtils.toSimpleName(input);

        // Then
        assertThat(actual).isEqualTo("");
    }

    @Test
    void shouldReturnEmptyWhenEmptyInput() {
        // Given
        String input = "";

        // When
        String actual = TypeUtils.toSimpleName(input);

        // Then
        assertThat(actual).isEqualTo("");
    }

    @Test
    void shouldCorrectlyConvertToSimpleNameWithTypeTrie() {
        // Given
        String input = "com.test.MyType";
        Trie typeTrie = new TrieImpl(null, null, null);

        // When
        String actual = TypeUtils.toSimpleName(input, typeTrie);

        // Then
        assertThat(actual).isEqualTo("MyType");
    }

    @Test
    void shouldCorrectlyConvertToSimpleNameWithTypeTrieAndListItemType() {
        // Given
        Trie typeTrie = new TrieImpl(null, "MyItemType", null);
        String input = "com.test.MyType";

        // When
        String actual = TypeUtils.toSimpleName(input, typeTrie);

        // Then
        assertThat(actual).isEqualTo("List<MyItemType>");
    }

    @Test
    void shouldCorrectlyConvertToSimpleNameWithTypeTrieAndDisplayName() {
        // Given
        Trie typeTrie = new TrieImpl(null, null, "MyTypeDisplayName");
        String input = "com.test.MyType";

        // When
        String actual = TypeUtils.toSimpleName(input, typeTrie);

        // Then
        assertThat(actual).isEqualTo("MyTypeDisplayName");
    }

    @Test
    void shouldCorrectlyConvertToSimpleNameWithTypeTrieAndListItemTypeAndDisplayName() {
        // Given
        Trie typeTrie = new TrieImpl(null, "MyItemType", "MyTypeDisplayName");
        String input = "com.test.MyType";

        // When
        String actual = TypeUtils.toSimpleName(input, typeTrie);

        // Then: we expect to still use the display name despite the list item type.
        assertThat(actual).isEqualTo("MyTypeDisplayName");
    }

    @Test
    void shouldCorrectlyConvertToSimpleNameWithTypeTries() {
        // Given
        String input = "com.test.MyType";
        Trie typeTrie = new TrieImpl(null, "MyItemType", "MyTypeDisplayName");
        Map<String, Trie> typeAndTrie = new HashMap<>();
        typeAndTrie.put(input, typeTrie);

        TypeAndTries typeAndTries = new TypeAndTries(typeAndTrie);

        // When
        String actual = TypeUtils.toSimpleName(input, typeAndTries);

        // Then
        assertThat(actual).isEqualTo("MyTypeDisplayName");
    }

    @Test
    void shouldCorrectlyConvertToSimpleNameWithTypeTriesAndTypeNotPresent() {
        // Given
        String input = "com.test.UnknownType";
        TypeAndTries typeAndTries = new TypeAndTries();

        // When
        String actual = TypeUtils.toSimpleName(input, typeAndTries);

        // Then
        assertThat(actual).isEqualTo("UnknownType");
    }

    @Test
    void shouldCorrectlyConvertToSimpleNameWithTypeTriesAndNullType() {
        // Given
        String input = null;
        TypeAndTries typeAndTries = new TypeAndTries();

        // When
        String actual = TypeUtils.toSimpleName(input, typeAndTries);

        // Then
        assertThat(actual).isEqualTo("");
    }
}
