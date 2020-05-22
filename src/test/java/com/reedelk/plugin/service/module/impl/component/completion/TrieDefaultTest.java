package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.runtime.api.message.MessageAttributes;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.FUNCTION;
import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;
import static com.reedelk.plugin.service.module.impl.component.completion.SuggestionTestUtils.createFunctionSuggestion;
import static com.reedelk.plugin.service.module.impl.component.completion.SuggestionTestUtils.createPropertySuggestion;
import static org.assertj.core.api.Assertions.assertThat;

class TrieDefaultTest extends AbstractCompletionTest {

    @Test
    void shouldReturnCorrectSimpleName() {
        // Given
        Trie trie = new TrieDefault(TypeTestUtils.MyItemType.class.getName());

        // When
        String actual = trie.toSimpleName(typeAndTries);

        // Then
        assertThat(actual).isEqualTo("TypeTestUtils$MyItemType");
    }

    @Test
    void shouldReturnCorrectSimpleNameWhenDisplayNameIsGiven() {
        // Given
        Trie trie = new TrieDefault(TypeTestUtils.MyItemType.class.getName(), null, "MyDisplayName");

        // When
        String actual = trie.toSimpleName(typeAndTries);

        // Then
        assertThat(actual).isEqualTo("MyDisplayName");
    }

    @Test
    void shouldReturnCorrectSimpleNameWhenTypeExtendsMessageAttributes() {
        // Given
        Trie trie = new TrieDefault(TypeTestUtils.MyItemType.class.getName(), MessageAttributes.class.getName(), "MyDisplayName");

        // When
        String actual = trie.toSimpleName(typeAndTries);

        // Then
        assertThat(actual).isEqualTo("MessageAttributes");
    }

    @Test
    void shouldReturnCorrectSuggestions() {
        // Given
        Suggestion method1 = createFunctionSuggestion("method1", String.class.getName(), typeAndTries);
        Suggestion method2 = createFunctionSuggestion("method2", long.class.getName(), typeAndTries);
        Suggestion property1 = createPropertySuggestion("myProperty", byte[].class.getName(), typeAndTries);
        Trie trie = new TrieDefault(TypeTestUtils.MyItemType.class.getName());
        trie.insert(method1);
        trie.insert(method2);
        trie.insert(property1);

        // When
        Collection<Suggestion> suggestions = trie.autocomplete("", typeAndTries);

        // Then
        PluginAssertion.assertThat(suggestions).hasSize(4)
                .contains(FUNCTION,
                        "method1",
                        "method1",
                        String.class.getName(),
                        String.class.getSimpleName())
                .contains(FUNCTION,
                        "method2",
                        "method2",
                        long.class.getName(),
                        long.class.getSimpleName())
                .contains(FUNCTION,
                        "toString()",
                        "toString",
                        String.class.getName(),
                        String.class.getSimpleName())
                .contains(PROPERTY,
                        "myProperty",
                        "myProperty",
                        byte[].class.getName(),
                        "byte[]");
    }

    @Test
    void shouldReturnPropertiesFromInheritedTypeCorrectly() {
        // Given
        Suggestion method2 = createFunctionSuggestion("method2", long.class.getName(), typeAndTries);
        Suggestion property1 = createPropertySuggestion("myProperty", byte[].class.getName(), typeAndTries);
        Trie trie = new TrieDefault(TypeTestUtils.MyItemType.class.getName(), TypeTestUtils.MyItemType.class.getName(), null);
        trie.insert(method2);
        trie.insert(property1);

        // When
        Collection<Suggestion> suggestions = trie.autocomplete("", typeAndTries);

        // Then
        PluginAssertion.assertThat(suggestions).hasSize(4)
                .contains(FUNCTION, // inherited from MyItemType
                        "method1",
                        "method1",
                        String.class.getName(),
                        String.class.getSimpleName())
                .contains(FUNCTION,
                        "method2",
                        "method2",
                        long.class.getName(),
                        long.class.getSimpleName())
                .contains(FUNCTION,
                        "toString()",
                        "toString",
                        String.class.getName(),
                        String.class.getSimpleName())
                .contains(PROPERTY,
                        "myProperty",
                        "myProperty",
                        byte[].class.getName(),
                        "byte[]");
    }
}
