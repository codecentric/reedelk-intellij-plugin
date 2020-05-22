package com.reedelk.plugin.service.module.impl.component.completion;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TrieListTest extends AbstractCompletionTest {

    @Test
    void shouldReturnCorrectSimpleName() {
        // Given
        Trie trie = new TrieList(MyCustomListType.class.getName(), List.class.getName(), null, TypeTestUtils.MyItemType.class.getName());

        // When
        String actual = trie.toSimpleName(typeAndTries);

        // Then
        assertThat(actual).isEqualTo("List<TypeTestUtils$MyItemType>");
    }

    @Test
    void shouldReturnCorrectSimpleNameWhenDisplayNameIsPresent() {
        // Given
        Trie trie = new TrieList(MyCustomListType.class.getName(), List.class.getName(), "MyList", TypeTestUtils.MyItemType.class.getName());

        // When
        String actual = trie.toSimpleName(typeAndTries);

        // Then
        assertThat(actual).isEqualTo("MyList");
    }

    @Test
    void shouldReturnCorrectListItemType() {
        // Given

        // When

        // Then
    }

    static class MyCustomListType extends ArrayList<TypeTestUtils.MyItemType> {
    }
}
