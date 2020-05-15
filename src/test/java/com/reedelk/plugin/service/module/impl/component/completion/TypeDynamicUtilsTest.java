package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.runtime.api.message.MessageAttributes;
import com.reedelk.runtime.api.message.MessagePayload;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TypeDynamicUtilsTest {

    // Is dynamic type tests
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

    // Resolve dynamic type tests
    @Test
    void shouldResolveMessageAttributesTypeFromDescriptor() {
        // Given
        String expectedType = "com.test.MyType";
        ComponentOutputDescriptor outputDescriptor = new ComponentOutputDescriptor();
        outputDescriptor.setAttributes(expectedType);

        Suggestion attributes =
                SuggestionTestUtils.createFunctionSuggestion("attributes()", MessageAttributes.class.getName());

        // When
        List<String> attributeTypes = TypeDynamicUtils.resolve(attributes, outputDescriptor);

        // Then
        assertThat(attributeTypes).containsOnly(expectedType);
    }

    @Test
    void shouldResolveDefaultMessageAttributesTypeFromNullDescriptor() {
        // Given
        ComponentOutputDescriptor outputDescriptor = null;

        Suggestion attributes =
                SuggestionTestUtils.createFunctionSuggestion("attributes()", MessageAttributes.class.getName());

        // When
        List<String> attributeTypes = TypeDynamicUtils.resolve(attributes, outputDescriptor);

        // Then
        assertThat(attributeTypes).containsOnly(MessageAttributes.class.getName());
    }

    @Test
    void shouldResolveMessagePayloadTypeFromDescriptor() {
        // Given
        List<String> expectedTypes = Arrays.asList(Integer.class.getName(), "com.test.MyType");
        ComponentOutputDescriptor outputDescriptor = new ComponentOutputDescriptor();
        outputDescriptor.setPayload(expectedTypes);

        Suggestion payload =
                SuggestionTestUtils.createFunctionSuggestion("payload()", MessagePayload.class.getName());

        // When
        List<String> types = TypeDynamicUtils.resolve(payload, outputDescriptor);

        // Then
        assertThat(types).containsExactlyInAnyOrder(Integer.class.getName(), "com.test.MyType");
    }

    @Test
    void shouldResolveDefaultMessagePayloadTypeFromNullDescriptor() {
        // Given
        ComponentOutputDescriptor outputDescriptor = null;

        Suggestion payload =
                SuggestionTestUtils.createFunctionSuggestion("payload()", MessagePayload.class.getName());

        // When
        List<String> types = TypeDynamicUtils.resolve(payload, outputDescriptor);

        // Then the default payload is Object type.
        assertThat(types).containsOnly(Object.class.getName());
    }

    @Test
    void shouldThrowExceptionWhenSuggestionTypeIsNotDynamic() {
        // Given
        ComponentOutputDescriptor outputDescriptor = new ComponentOutputDescriptor();
        Suggestion method =
                SuggestionTestUtils.createFunctionSuggestion("method()", "com.test.MyType");

        // When
        IllegalStateException thrown =
                assertThrows(IllegalStateException.class, () -> TypeDynamicUtils.resolve(method, outputDescriptor));


        // Then
        assertThat(thrown).hasMessage("Resolve must be called only if the suggestion type is dynamic");
    }
}
