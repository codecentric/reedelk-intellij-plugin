package com.reedelk.plugin.assertion.suggestion;

import com.reedelk.plugin.service.module.impl.component.completion.Suggestion;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class SuggestionAssertion {

    private final Collection<Suggestion> suggestions;

    public SuggestionAssertion(Collection<Suggestion> suggestions) {
        this.suggestions = suggestions;
    }

    public SuggestionAssertion contains(String lookupString, String typeText) {
        boolean found = suggestions.stream()
                .anyMatch(suggestion ->
                        lookupString.equals(suggestion.getLookup()) &&
                        typeText.equals(suggestion.getReturnType()));
        assertThat(found).isTrue();
        return this;
    }

    public SuggestionAssertion contains(Suggestion expectedSuggestion) {
        boolean found = suggestions.stream()
                .anyMatch(suggestion -> suggestion == expectedSuggestion);
        assertThat(found).isTrue();
        return this;
    }

    public SuggestionAssertion containsOnly(Suggestion expectedSuggestion) {
        assertThat(suggestions).hasSize(1);
        boolean found = suggestions.stream()
                .anyMatch(suggestion -> suggestion == expectedSuggestion);
        assertThat(found).isTrue();
        return this;
    }

    public SuggestionAssertion hasSize(int expectedSize) {
        assertThat(suggestions).hasSize(expectedSize);
        return this;
    }

    public SuggestionAssertion isEmpty() {
        assertThat(suggestions).isEmpty();
        return this;
    }
}
