package com.reedelk.plugin.assertion.suggestion;

import com.reedelk.plugin.service.module.impl.component.completion.Suggestion;

import java.util.Collection;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class SuggestionAssertion {

    private final Collection<Suggestion> suggestions;

    public SuggestionAssertion(Collection<Suggestion> suggestions) {
        this.suggestions = suggestions;
    }

    public SuggestionAssertion contains(Suggestion.Type expectedType,
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
