package com.esb.plugin.editor.properties.widget.input.script.suggestion;

import com.esb.plugin.assertion.PluginAssertion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SuggestionTreeTest {

    private SuggestionTree suggestionTree;

    @BeforeEach
    void setUp() {
        this.suggestionTree = new SuggestionTree();
        this.suggestionTree.insert(new SuggestionToken("Abc", SuggestionType.VARIABLE));
        this.suggestionTree.insert(new SuggestionToken("Aabc", SuggestionType.VARIABLE));
        this.suggestionTree.insert(new SuggestionToken("Aabbc", SuggestionType.PROPERTY));
        this.suggestionTree.insert(new SuggestionToken("Aabbcc", SuggestionType.PROPERTY));
        this.suggestionTree.insert(new SuggestionToken("Bcd", SuggestionType.VARIABLE));
        this.suggestionTree.insert(new SuggestionToken("Bbcd", SuggestionType.PROPERTY));
        this.suggestionTree.insert(new SuggestionToken("Bbccd", SuggestionType.PROPERTY));
        this.suggestionTree.insert(new SuggestionToken("Bbccdd", SuggestionType.VARIABLE));
    }

    @Test
    void shouldSearchByPrefixReturnEmptyWhenEmpty() {
        // Given
        SuggestionTree suggestionTree = new SuggestionTree();

        // When
        Set<Suggestion> suggestions = suggestionTree.searchByPrefix("f");

        // Then
        assertThat(suggestions).isEmpty();
    }

    @Test
    void shouldSearchByPrefixReturnCorrectSuggestions1() {
        // Expect
        PluginAssertion.assertThat(suggestionTree).searchByPrefix("A")
                .hasNumberOfResults(4)
                .hasResult("Abc", SuggestionType.VARIABLE)
                .hasResult("Aabc", SuggestionType.VARIABLE)
                .hasResult("Aabbc", SuggestionType.PROPERTY)
                .hasResult("Aabbcc", SuggestionType.PROPERTY);
    }

    @Test
    void shouldSearchByPrefixReturnCorrectSuggestions2() {
        // Expect
        PluginAssertion.assertThat(suggestionTree).searchByPrefix("Aabb")
                .hasNumberOfResults(2)
                .hasResult("Aabbc", SuggestionType.PROPERTY)
                .hasResult("Aabbcc", SuggestionType.PROPERTY);
    }

    @Test
    void shouldSearchByPrefixReturnCorrectSuggestions3() {
        // Expect
        PluginAssertion.assertThat(suggestionTree).searchByPrefix("Bbccd")
                .hasNumberOfResults(2)
                .hasResult("Bbccd", SuggestionType.PROPERTY)
                .hasResult("Bbccdd", SuggestionType.VARIABLE);
    }
}