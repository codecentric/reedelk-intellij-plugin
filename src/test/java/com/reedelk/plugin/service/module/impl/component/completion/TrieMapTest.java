package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.plugin.assertion.PluginAssertion;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.FUNCTION;
import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;
import static org.assertj.core.api.Assertions.assertThat;

class TrieMapTest extends AbstractCompletionTest {

    @Test
    void shouldReturnCorrectSimpleName() {
        // Given
        Trie trie = new TrieMap(
                MyCustomMapType.class.getName(),
                HashMap.class.getName(),
                null,
                String.class.getName(),
                TypeTestUtils.MyItemType.class.getName());

        // When
        String actual = trie.toSimpleName(typeAndTries);

        // Then
        assertThat(actual).isEqualTo("Map<String,TypeTestUtils$MyItemType>");
    }

    @Test
    void shouldReturnCorrectSimpleNameWhenDisplayNameIsPresent() {
        // Given
        Trie trie = new TrieMap(
                MyCustomMapType.class.getName(),
                HashMap.class.getName(),
                "MyMap",
                String.class.getName(),
                TypeTestUtils.MyItemType.class.getName());

        // When
        String actual = trie.toSimpleName(typeAndTries);

        // Then
        assertThat(actual).isEqualTo("MyMap");
    }

    @Test
    void shouldReturnCorrectMapKeyType() {
        // Given
        Trie trie = new TrieMap(
                MyCustomMapType.class.getName(),
                HashMap.class.getName(),
                null,
                String.class.getName(),
                TypeTestUtils.MyItemType.class.getName());

        // When
        TypeProxy mapKeyType = trie.mapKeyType(typeAndTries);

        // Then
        assertThat(mapKeyType.getTypeFullyQualifiedName()).isEqualTo(String.class.getName());
    }

    @Test
    void shouldReturnCorrectMapValueType() {
        // Given
        Trie trie = new TrieMap(
                MyCustomMapType.class.getName(),
                HashMap.class.getName(),
                null,
                String.class.getName(),
                TypeTestUtils.MyItemType.class.getName());

        // When
        TypeProxy mapValueType = trie.mapValueType(typeAndTries);

        // Then
        assertThat(mapValueType.getTypeFullyQualifiedName()).isEqualTo(TypeTestUtils.MyItemType.class.getName());
    }

    @Test
    void shouldReturnIsMapTrue() {
        // Given
        Trie trie = new TrieMap(
                MyCustomMapType.class.getName(),
                HashMap.class.getName(),
                null,
                String.class.getName(),
                TypeTestUtils.MyItemType.class.getName());

        // Expect
        assertThat(trie.isMap()).isTrue();
    }

    @Test
    void shouldReturnCorrectSuggestions() {
        // Given
        Trie trie = typeAndTries.getOrDefault(TypeTestUtils.MapSecondType.class.getName());

        // When
        Collection<Suggestion> suggestions = trie.autocomplete("", typeAndTries);

        // Then
        PluginAssertion.assertThat(suggestions).hasSize(5)
                .contains(PROPERTY,
                        "secondProperty1",
                        "secondProperty1",
                        String.class.getName(),
                        String.class.getSimpleName())
                .contains(PROPERTY,
                        "secondProperty2",
                        "secondProperty2",
                        long.class.getName(),
                        long.class.getSimpleName())
                .contains(FUNCTION,
                        "toString()",
                        "toString",
                        String.class.getName(),
                        String.class.getSimpleName())
                .contains(FUNCTION,
                        "eachWithIndex { entry, i ->  }",
                        "eachWithIndex",
                        TypeTestUtils.MapSecondType.class.getName(),
                        "Map<String,Serializable>")
                .contains(FUNCTION,
                        "each { entry }",
                        "each",
                        TypeTestUtils.MapSecondType.class.getName(),
                        "Map<String,Serializable>");
    }

    @Test
    void shouldReturnCorrectSuggestionsForClosureArgument() {
        // Given
        Trie trie = typeAndTries.getOrDefault(TypeTestUtils.MapSecondType.class.getName());

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
                        "entry",
                        "entry",
                        Serializable.class.getName(),
                        Serializable.class.getSimpleName())
                .contains(PROPERTY,
                        "i",
                        "i",
                        String.class.getName(),
                        String.class.getSimpleName());
    }

    static class MyCustomMapType extends HashMap<String,TypeTestUtils.MyItemType> {
    }
}
