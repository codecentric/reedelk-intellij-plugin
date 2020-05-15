package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.runtime.api.message.MessageAttributes;
import com.reedelk.runtime.api.message.MessagePayload;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.FUNCTION;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
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
        outputDescriptor.setAttributes(singletonList(expectedType));

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
        List<String> expectedTypes = asList(Integer.class.getName(), "com.test.MyType");
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

    // Create dynamic suggestions
    @Test
    void shouldCorrectlyCreateDynamicSuggestions() {
        // Given
        String myType1 = "com.test.MyType1";
        Trie myType1Trie = new TrieImpl(null, null, null);
        String myType2 = "com.test.MyType2";
        Trie myType2Trie = new TrieImpl(null, null, null);

        Map<String, Trie> moduleTries = new HashMap<>();
        moduleTries.put(myType1, myType1Trie);
        moduleTries.put(myType2, myType2Trie);

        TypeAndTries typeAndTries = new TypeAndTries(moduleTries);
        ComponentOutputDescriptor outputDescriptor = new ComponentOutputDescriptor();
        outputDescriptor.setPayload(asList(myType1, myType2));
        Suggestion payload =
                SuggestionTestUtils.createFunctionSuggestion("payload()", MessagePayload.class.getName());

        // When
        Collection<Suggestion> dynamicSuggestions =
                TypeDynamicUtils.createDynamicSuggestions(outputDescriptor, payload, typeAndTries);

        // Then
        assertThat(dynamicSuggestions).hasSize(2);
        PluginAssertion.assertThat(dynamicSuggestions)
                .contains(FUNCTION, "payload()", "payload()", myType1, "MyType1")
                .contains(FUNCTION, "payload()", "payload()", myType2, "MyType2");
    }
}
