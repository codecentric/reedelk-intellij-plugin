package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.plugin.assertion.PluginAssertion;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.FUNCTION;
import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;
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
        Trie trie = new TrieList(MyCustomListType.class.getName(), List.class.getName(), "MyList", TypeTestUtils.MyItemType.class.getName());

        // When
        TypeProxy listItemType = trie.listItemType(typeAndTries);

        // Then
        assertThat(listItemType.getTypeFullyQualifiedName()).isEqualTo(TypeTestUtils.MyItemType.class.getName());
    }

    @Test
    void shouldReturnCorrectSuggestions() {
        // Given
        Trie trie = typeAndTries.getOrDefault(TypeTestUtils.ListMyItemType.class.getName());

        // When
        Collection<Suggestion> suggestions = trie.autocomplete("", typeAndTries);

        // Then
        PluginAssertion.assertThat(suggestions).hasSize(4)
                .contains(FUNCTION,
                        "toString()",
                        "toString",
                        String.class.getName(),
                        String.class.getSimpleName())
                .contains(FUNCTION,
                        "eachWithIndex { it, i ->  }",
                        "eachWithIndex",
                        TypeTestUtils.ListMyItemType.class.getName(),
                        "List<TypeTestUtils$MyItemType>")
                .contains(FUNCTION,
                        "collect { it }",
                        "collect",
                        TypeTestUtils.ListMyItemType.class.getName(),
                        "List<TypeTestUtils$MyItemType>")
                .contains(FUNCTION,
                        "each { it }",
                        "each",
                        TypeTestUtils.ListMyItemType.class.getName(),
                        "List<TypeTestUtils$MyItemType>");
    }

    @Test
    void shouldReturnIsListTrue() {
        // Given
        Trie trie = new TrieList(MyCustomListType.class.getName(), List.class.getName(), null, TypeTestUtils.MyItemType.class.getName());

        // Expect
        assertThat(trie.isList()).isTrue();
    }

    @Test
    void shouldReturnCorrectSuggestionsForClosureArgument() {
        // Given
        Trie trie = typeAndTries.getOrDefault(TypeTestUtils.ListMyItemType.class.getName());

        // When
        Collection<Suggestion> suggestions = trie.autocomplete("ea", typeAndTries);
        Trie closureTrie = suggestions.iterator().next().getReturnType().resolve(typeAndTries);
        Collection<Suggestion> closureSuggestions = closureTrie.autocomplete("{", typeAndTries);
        Trie closureArgumentsTrie = closureSuggestions.iterator().next().getReturnType().resolve(typeAndTries);
        Collection<Suggestion> closureArguments = closureArgumentsTrie.autocomplete("", typeAndTries);

        // Then
        // each and eachWithIndex
        PluginAssertion.assertThat(closureArguments).hasSize(2)
                .contains(PROPERTY,
                        "it",
                        "it",
                        TypeTestUtils.MyItemType.class.getName(),
                        "TypeTestUtils$MyItemType")
                .contains(PROPERTY,
                        "i",
                        "i",
                        int.class.getName(),
                        int.class.getSimpleName());
    }

    static class MyCustomListType extends ArrayList<TypeTestUtils.MyItemType> {
    }
}
