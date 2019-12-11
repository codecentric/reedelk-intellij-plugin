package com.reedelk.plugin.assertion.suggestion;

import com.reedelk.plugin.service.module.impl.completion.Suggestion;
import com.reedelk.plugin.service.module.impl.completion.SuggestionType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SuggestionAssertion {

    private final List<Suggestion> suggestions;

    public SuggestionAssertion(List<Suggestion> suggestions) {
        this.suggestions = suggestions;
    }


    public SuggestionAssertion isEmpty() {
        assertThat(suggestions).isEmpty();
        return this;
    }

    public SuggestionAssertion contains(String token, SuggestionType suggestionType, String typeName) {
        assertThat(suggestions).contains(Suggestion.from(token, suggestionType, typeName, null));
        return this;
    }

    public SuggestionAssertion containsOnly(String token, SuggestionType suggestionType, String typeName) {
        assertThat(suggestions).containsExactly(Suggestion.from(token, suggestionType, typeName, null));
        return this;
    }

    public SuggestionAssertion contains(String token, SuggestionType suggestionType, String typeName, int offset) {
        assertThat(suggestions).contains(Suggestion.from(token, suggestionType, typeName, offset));
        return this;
    }

    public SuggestionAssertion containsOnly(String token, SuggestionType suggestionType, String typeName, int offset) {
        assertThat(suggestions).containsExactly(Suggestion.from(token, suggestionType, typeName, offset));
        return this;
    }

    public SuggestionAssertion hasSize(int expectedSize) {
        assertThat(suggestions).hasSize(expectedSize);
        return this;
    }
}
