package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.plugin.assertion.PluginAssertion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

class TrieImplTest {

    private TypeAndTries typeAndTries;

    @BeforeEach
    void setUp() {
        typeAndTries = new TypeAndTries();
    }

    @Test
    void shouldFindMultipleSuggestionsByPrefix() {
        // Given
        Suggestion suggestion1 = Suggestion.create(Suggestion.Type.PROPERTY)
                .insertValue("property1")
                .returnType(String.class.getName())
                .returnTypeDisplayValue(String.class.getSimpleName())
                .build();

        Suggestion suggestion2 = Suggestion.create(Suggestion.Type.PROPERTY)
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
        Suggestion suggestion1 = Suggestion.create(Suggestion.Type.FUNCTION)
                .insertValue("method1()")
                .lookupToken("method1")
                .tailText("()")
                .returnType(String.class.getName())
                .returnTypeDisplayValue(String.class.getSimpleName())
                .build();

        Suggestion suggestion2 = Suggestion.create(Suggestion.Type.FUNCTION)
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
}
