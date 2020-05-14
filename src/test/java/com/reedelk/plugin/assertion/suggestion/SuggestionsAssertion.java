package com.reedelk.plugin.assertion.suggestion;

import com.reedelk.plugin.service.module.impl.component.completion.Suggestion;

import java.util.Collection;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class SuggestionsAssertion {

    private final Collection<Suggestion> suggestions;

    public SuggestionsAssertion(Collection<Suggestion> suggestions) {
        this.suggestions = suggestions;
    }

    public SuggestionsAssertion contains(Suggestion.Type expectedType,
                                         String expectedLookup,
                                         String expectedLookupDisplayValue,
                                         String expectedReturnType,
                                         String expectedReturnTypeDisplayValue) {
        boolean found = suggestions.stream().anyMatch(suggestion -> {
            Suggestion.Type actualType = suggestion.getType();
            String actualLookup = suggestion.getInsertValue();
            String actualLookupDisplayValue = suggestion.getLookupToken();
            String actualReturnType = suggestion.getReturnType();
            String actualReturnTypeDisplayValue = suggestion.getReturnTypeDisplayValue();
            return Objects.equals(actualType, expectedType) &&
                    Objects.equals(actualLookup, expectedLookup) &&
                    Objects.equals(actualLookupDisplayValue, expectedLookupDisplayValue) &&
                    Objects.equals(actualReturnType, expectedReturnType) &&
                    Objects.equals(actualReturnTypeDisplayValue, expectedReturnTypeDisplayValue);
                });
        assertThat(found).isTrue();
        return this;
    }

    public SuggestionsAssertion contains(Suggestion expectedSuggestion) {
        boolean found = suggestions.stream()
                .anyMatch(suggestion -> suggestion == expectedSuggestion);
        assertThat(found).isTrue();
        return this;
    }

    public SuggestionsAssertion containsOnly(Suggestion expectedSuggestion) {
        assertThat(suggestions).hasSize(1);
        boolean found = suggestions.stream()
                .anyMatch(suggestion -> suggestion == expectedSuggestion);
        assertThat(found).isTrue();
        return this;
    }

    public SuggestionsAssertion hasSize(int expectedSize) {
        assertThat(suggestions).hasSize(expectedSize);
        return this;
    }

    public SuggestionsAssertion isEmpty() {
        assertThat(suggestions).isEmpty();
        return this;
    }
}
