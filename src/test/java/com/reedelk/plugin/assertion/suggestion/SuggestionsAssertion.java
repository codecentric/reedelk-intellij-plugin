package com.reedelk.plugin.assertion.suggestion;

import com.reedelk.plugin.service.module.impl.component.completion.Suggestion;
import com.reedelk.plugin.service.module.impl.component.completion.TypeProxy;

import java.util.Collection;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class SuggestionsAssertion {

    private final Collection<Suggestion> suggestions;

    public SuggestionsAssertion(Collection<Suggestion> suggestions) {
        this.suggestions = suggestions;
    }

    public SuggestionsAssertion contains(Suggestion.Type expectedType,
                                         String expectedInsertValue,
                                         String expectedLookupDisplayValue,
                                         String expectedReturnTypeFullyQualifiedName,
                                         String expectedReturnTypeDisplayValue) {
        boolean found = suggestions.stream().anyMatch(suggestion -> {
            Suggestion.Type actualType = suggestion.getType();
            String actualInsertValue = suggestion.getInsertValue();
            String actualLookupToken = suggestion.getLookupToken();
            TypeProxy actualTypeProxy = suggestion.getReturnType();
            String actualReturnTypeDisplayValue = suggestion.getReturnTypeDisplayValue();
            return Objects.equals(actualType, expectedType) &&
                    Objects.equals(actualInsertValue, expectedInsertValue) &&
                    Objects.equals(actualLookupToken, expectedLookupDisplayValue) &&
                    Objects.equals(actualTypeProxy, TypeProxy.create(expectedReturnTypeFullyQualifiedName)) &&
                    Objects.equals(actualReturnTypeDisplayValue, expectedReturnTypeDisplayValue);
                });
        assertThat(found)
                .withFailMessage("Could not find suggestion from collection: " + suggestions.toString())
                .isTrue();
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
