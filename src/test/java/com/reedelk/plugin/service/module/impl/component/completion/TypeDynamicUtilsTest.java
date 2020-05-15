package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.runtime.api.message.MessageAttributes;
import com.reedelk.runtime.api.message.MessagePayload;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TypeDynamicUtilsTest {

    @Test
    void shouldReturnTrueWhenSuggestionHasMessagePayloadReturnType() {
        // Given
        Suggestion payload =
                SuggestionTestUtils.createFunctionSuggestion("payload()", MessagePayload.class.getName());

        // When
        boolean actual = TypeDynamicUtils.is(payload);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void shouldReturnTrueWhenSuggestionHasMessageAttributeReturnType() {
        // Given
        Suggestion attributes =
                SuggestionTestUtils.createFunctionSuggestion("attributes()", MessageAttributes.class.getName());

        // When
        boolean actual = TypeDynamicUtils.is(attributes);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void shouldReturnFalseWhenSuggestionHasStringReturnType() {
        // Given
        Suggestion method =
                SuggestionTestUtils.createFunctionSuggestion("method()", String.class.getName());

        // When
        boolean actual = TypeDynamicUtils.is(method);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void shouldReturnFalseWhenReturnTypeIsEmpty() {
        // Given
        Suggestion method =
                SuggestionTestUtils.createFunctionSuggestion("method()", "");

        // When
        boolean actual = TypeDynamicUtils.is(method);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void shouldReturnFalseWhenReturnTypeIsVoid() {
        // Given
        Suggestion method =
                SuggestionTestUtils.createFunctionSuggestion("method()", Default.DEFAULT_RETURN_TYPE);

        // When
        boolean actual = TypeDynamicUtils.is(method);

        // Then
        assertThat(actual).isFalse();
    }
}
