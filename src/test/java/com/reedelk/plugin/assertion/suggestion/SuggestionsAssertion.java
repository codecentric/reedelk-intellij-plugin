package com.reedelk.plugin.assertion.suggestion;

import com.reedelk.plugin.service.module.impl.component.completion.Suggestion;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class SuggestionsAssertion {

    private final Collection<Suggestion> suggestions;

    public SuggestionsAssertion(Collection<Suggestion> suggestions) {
        this.suggestions = suggestions;
    }

    public SuggestionsAssertion contains(Suggestion.Type expectedType,
                                         String expectedInsertValue,
                                         String expectedLookupToken,
                                         String expectedReturnTypeFullyQualifiedName,
                                         String expectedReturnTypeDisplayValue) {
        boolean found = suggestions.stream().anyMatch(suggestion -> {
            Suggestion.Type actualType = suggestion.getType();
            String actualInsertValue = suggestion.getInsertValue();
            String actualLookupToken = suggestion.getLookupToken();
            String actualTypeFullyQualifiedName = suggestion.getReturnType().getTypeFullyQualifiedName();
            String actualReturnTypeDisplayValue = suggestion.getReturnTypeDisplayValue();
            return Objects.equals(actualType, expectedType) &&
                    Objects.equals(actualInsertValue, expectedInsertValue) &&
                    Objects.equals(actualLookupToken, expectedLookupToken) &&
                    sameReturnType(actualTypeFullyQualifiedName, expectedReturnTypeFullyQualifiedName) &&
                    sameReturnDisplayType(actualReturnTypeDisplayValue, expectedReturnTypeDisplayValue);
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

    // There might be multiple return types e.g Type1,Type2 and so on. We want to compare them
    // without explicitly consider the order, e.g Type1,Type2 == Type2,Type1
    private boolean sameReturnDisplayType(String actualReturnTypeDisplayValue, String expectedReturnTypeDisplayValue) {
        String[] actualReturnDisplayTypeSegments = actualReturnTypeDisplayValue.split(",");
        String[] expectedReturnDisplayTypeSegments = expectedReturnTypeDisplayValue.split(",");
        return new HashSet<>(Arrays.asList(actualReturnDisplayTypeSegments))
                .equals(new HashSet<>(Arrays.asList(expectedReturnDisplayTypeSegments)));
    }

    private boolean sameReturnType(String actualReturnType, String expectedReturnType) {
        String[] actualReturnTypeSegments = actualReturnType.split(",");
        String[] expectedReturnTypeSegments = expectedReturnType.split(",");
        return new HashSet<>(Arrays.asList(actualReturnTypeSegments))
                .equals(new HashSet<>(Arrays.asList(expectedReturnTypeSegments)));
    }
}
