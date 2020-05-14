package com.reedelk.plugin.assertion.suggestion;

import com.reedelk.plugin.service.module.impl.component.completion.Suggestion;

import static org.assertj.core.api.Assertions.assertThat;

public class SuggestionAssertion {

    private final Suggestion suggestion;

    public SuggestionAssertion(Suggestion suggestion) {
        this.suggestion = suggestion;
    }

    public SuggestionAssertion hasType(Suggestion.Type expectedType) {
        Suggestion.Type actualType = suggestion.getType();
        assertThat(actualType).isEqualTo(expectedType);
        return this;
    }

    public SuggestionAssertion hasReturnType(String expectedReturnType) {
        String actualReturnType = suggestion.getReturnType();
        assertThat(actualReturnType).isEqualTo(expectedReturnType);
        return this;
    }

    public SuggestionAssertion hasReturnDisplayValue(String expectedReturnDisplayValue) {
        String actualReturnDisplayValue = suggestion.getReturnTypeDisplayValue();
        assertThat(actualReturnDisplayValue).isEqualTo(expectedReturnDisplayValue);
        return this;
    }

    public SuggestionAssertion hasInsertValue(String expectedInsertValue) {
        String actualInsertValue = suggestion.getInsertValue();
        assertThat(actualInsertValue).isEqualTo(expectedInsertValue);
        return this;
    }

    public SuggestionAssertion hasLookupToken(String expectedLookupToken) {
        String actualLookupToken = suggestion.getLookupToken();
        assertThat(actualLookupToken).isEqualTo(expectedLookupToken);
        return this;
    }

    public SuggestionAssertion hasTailText(String expectedTailText) {
        String actualTailText = suggestion.getTailText();
        assertThat(actualTailText).isEqualTo(expectedTailText);
        return this;
    }

    public SuggestionAssertion hasOffset(int expectedOffset) {
        int actualOffset = suggestion.getCursorOffset();
        assertThat(actualOffset).isEqualTo(expectedOffset);
        return this;
    }
}
