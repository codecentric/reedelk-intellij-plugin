package com.reedelk.plugin.assertion.trie;

import com.reedelk.plugin.editor.properties.widget.input.script.suggestion.Suggestion;
import com.reedelk.plugin.editor.properties.widget.input.script.suggestion.SuggestionType;

import java.util.Set;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class SearchByPrefixResultAssertion {

    private final Set<Suggestion> suggestions;

    public SearchByPrefixResultAssertion(Set<Suggestion> suggestions) {
        this.suggestions = suggestions;
    }

    public SearchByPrefixResultAssertion hasNumberOfResults(int resultSize) {
        assertThat(suggestions).hasSize(resultSize);
        return this;
    }

    public SearchByPrefixResultAssertion hasResult(String expectedToken, SuggestionType expectedType) {
        for (Suggestion suggestion : suggestions) {
            if (expectedToken.equals(suggestion.getToken()) &&
                    expectedType.equals(suggestion.getSuggestionType())) {
                return this;
            }
        }
        fail(format("Could not find suggestion matching token=%s and type=%s", expectedToken, expectedType));
        return this;
    }
}
