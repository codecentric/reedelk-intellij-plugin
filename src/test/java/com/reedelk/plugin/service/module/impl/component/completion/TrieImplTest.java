package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.plugin.assertion.PluginAssertion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.FUNCTION;
import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;

class TrieImplTest {

    private TypeAndTries typeAndTries;

    @BeforeEach
    void setUp() {
        typeAndTries = new TypeAndTries();
    }

    @Test
    void shouldFindMultipleSuggestionsByPrefix() {
        // Given
        Suggestion suggestion1 = Suggestion.create(PROPERTY)
                .insertValue("property1")
                .returnType(String.class.getName())
                .returnTypeDisplayValue(String.class.getSimpleName())
                .build();

        Suggestion suggestion2 = Suggestion.create(PROPERTY)
                .insertValue("property2")
                .returnType(String.class.getName())
                .returnTypeDisplayValue(String.class.getSimpleName())
                .build();

        TrieImpl trie = new TrieImpl();
        trie.insert(suggestion1);
        trie.insert(suggestion2);

        Collection<Suggestion> suggestions = trie.autocomplete("", typeAndTries);
        PluginAssertion.assertThat(suggestions)
                .hasSize(2);
    }

    @Test
    void shouldAddMultipleSuggestionsWithDifferentSignature() {
        // Given
        Suggestion suggestion1 = Suggestion.create(FUNCTION)
                .insertValue("method1()")
                .lookupToken("method1")
                .tailText("()")
                .returnType(String.class.getName())
                .returnTypeDisplayValue(String.class.getSimpleName())
                .build();

        Suggestion suggestion2 = Suggestion.create(FUNCTION)
                .insertValue("method1()")
                .lookupToken("method1")
                .tailText("(String key)")
                .returnType(String.class.getName())
                .returnTypeDisplayValue(String.class.getSimpleName())
                .build();

        TrieImpl trie = new TrieImpl();
        trie.insert(suggestion1);
        trie.insert(suggestion2);

        Collection<Suggestion> suggestions = trie.autocomplete("", typeAndTries);
        PluginAssertion.assertThat(suggestions)
                .hasSize(2);
    }

    @Test
    void shouldFindCorrectSuggestionsForPrefix() {
        // Given
        Suggestion suggestion1 = Suggestion.create(FUNCTION)
                .insertValue("method1()")
                .lookupToken("method1")
                .tailText("()")
                .returnType(String.class.getName())
                .returnTypeDisplayValue(String.class.getSimpleName())
                .build();

        Suggestion suggestion2 = Suggestion.create(FUNCTION)
                .insertValue("find()")
                .lookupToken("find")
                .tailText("(String key)")
                .returnType(String.class.getName())
                .returnTypeDisplayValue(String.class.getSimpleName())
                .build();

        Suggestion suggestion3 = Suggestion.create(PROPERTY)
                .insertValue("measure")
                .lookupToken("measure")
                .returnType(String.class.getName())
                .returnTypeDisplayValue(String.class.getSimpleName())
                .build();

        Suggestion suggestion4 = Suggestion.create(PROPERTY)
                .insertValue("measured")
                .lookupToken("measured")
                .returnType(String.class.getName())
                .returnTypeDisplayValue(String.class.getSimpleName())
                .build();

        TrieImpl trie = new TrieImpl();
        trie.insert(suggestion1);
        trie.insert(suggestion2);
        trie.insert(suggestion3);
        trie.insert(suggestion4);

        Collection<Suggestion> suggestions = trie.autocomplete("me", typeAndTries);
        PluginAssertion.assertThat(suggestions).hasSize(3);

        PluginAssertion.assertThat(suggestions)
                .contains(PROPERTY, "measure", "measure", String.class.getName(), String.class.getSimpleName())
                .contains(PROPERTY, "measured", "measured", String.class.getName(), String.class.getSimpleName())
                .contains(FUNCTION, "method1()", "method1", String.class.getName(), String.class.getSimpleName());
    }
}
