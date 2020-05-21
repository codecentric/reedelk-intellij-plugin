package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutput;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutputDefault;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.FUNCTION;
import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;
import static com.reedelk.plugin.service.module.impl.component.completion.TypeTestUtils.MapFirstType;
import static com.reedelk.runtime.api.commons.StringUtils.EMPTY;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class SuggestionFinderTest {

    private static Trie MESSAGE_AND_CONTEXT_TRIE = TypeDefault.MESSAGE_AND_CONTEXT;
    private static SuggestionFinder FINDER;

    @BeforeAll
    static void setUp() {
        Map<String, Trie> defaultTypesAndTries = new HashMap<>();

        TypeTestUtils.ALL_TYPES.forEach(trieProvider -> trieProvider.register(defaultTypesAndTries));

        TypeAndTries typeAndTries = new TypeAndTries(defaultTypesAndTries);

        FINDER = new SuggestionFinder(typeAndTries);
    }

    // Verify dynamic return type computed correctly.
    @Test
    void shouldReturnCorrectSuggestionsForTypeString() {
        // Given
        List<String> attributes = emptyList();
        List<String> payload = singletonList(String.class.getName());
        PreviousComponentOutput output = new PreviousComponentOutputDefault(attributes, payload, EMPTY);
        String[] tokens = new String[] {"message", "paylo"};

        // When
        Collection<Suggestion> suggestions = FINDER.suggest(MESSAGE_AND_CONTEXT_TRIE, tokens, output);

        // Then
        PluginAssertion.assertThat(suggestions).hasSize(1)
                .contains(FUNCTION,"payload", "payload", String.class.getName(), String.class.getSimpleName());
    }

    @Test
    void shouldReturnCorrectSuggestionsForTypeMap() {
        // Given
        List<String> attributes = emptyList();
        List<String> payload = singletonList(MapFirstType.class.getName());
        PreviousComponentOutput output = new PreviousComponentOutputDefault(attributes, payload, EMPTY);
        String[] tokens = new String[] {"message", "paylo"};

        // When
        Collection<Suggestion> suggestions = FINDER.suggest(MESSAGE_AND_CONTEXT_TRIE, tokens, output);

        // Then
        PluginAssertion.assertThat(suggestions).hasSize(1)
                .contains(FUNCTION, "payload", "payload", MapFirstType.class.getName(), MapFirstType.class.getSimpleName());
    }

    @Test
    void shouldReturnCorrectSuggestionsForClosure() {
        // Given
        List<String> attributes = emptyList();
        List<String> payload = singletonList(MapFirstType.class.getName());
        PreviousComponentOutput output = new PreviousComponentOutputDefault(attributes, payload, EMPTY);
        String[] tokens = new String[] {"message", "payload", "each", "{", ""};

        // When
        Collection<Suggestion> suggestions = FINDER.suggest(MESSAGE_AND_CONTEXT_TRIE, tokens, output);

        // Then the closure has two properties: 'i' and 'each' to iterate the map content. 'i' and 'each' must
        // have the type of the 'MapFirstType' (<String, Serializable>).
        PluginAssertion.assertThat(suggestions).hasSize(2)
                .contains(PROPERTY, "i", "i", String.class.getName(), String.class.getSimpleName())
                .contains(PROPERTY, "entry", "entry", Serializable.class.getName(), Serializable.class.getSimpleName());
    }

    @Test
    void shouldReturnCorrectMessagePayloadTypeListOfSomeKnownType() {
        // Given
        List<String> attributes = emptyList();
        List<String> payload = singletonList(TypeTestUtils.ListMyItemType.class.getName());
        PreviousComponentOutput output = new PreviousComponentOutputDefault(attributes, payload, EMPTY);
        String[] tokens = new String[] {"message", "paylo"};

        // When
        Collection<Suggestion> suggestions = FINDER.suggest(MESSAGE_AND_CONTEXT_TRIE, tokens, output);

        // Then
        PluginAssertion.assertThat(suggestions).hasSize(1)
                .contains(FUNCTION,
                        "payload",
                        "payload",
                        TypeTestUtils.ListMyItemType.class.getName(), "List<TypeTestUtils$MyItemType>");
    }

    // A type which was not registered in the type map tree structure.
    @Test
    void shouldReturnCorrectMessagePayloadTypeListOfSomeUnknownType() {
        // Given
        List<String> attributes = emptyList();
        List<String> payload = singletonList(TypeTestUtils.ListMyUnknownType.class.getName());
        PreviousComponentOutput descriptor = new PreviousComponentOutputDefault(attributes, payload, EMPTY);
        String[] tokens = new String[] {"message", "paylo"};

        // When
        Collection<Suggestion> suggestions = FINDER.suggest(MESSAGE_AND_CONTEXT_TRIE, tokens, descriptor);

        // Then
        PluginAssertion.assertThat(suggestions).hasSize(1)
                .contains(FUNCTION,
                        "payload",
                        "payload",
                        TypeTestUtils.ListMyItemType.class.getName(), "List<TypesTestUtils$MyUnknownType>");
    }

    /**
    @Test
    void shouldReturnCorrectMessagePayloadTypeListOfSomeMapType() {
        // Given
        ComponentOutputDescriptor descriptor = new ComponentOutputDescriptor();
        descriptor.setPayload(singletonList(ListMapFirstType.class.getName()));
        String[] tokens = new String[] {"message", "paylo"};

        // When
        Collection<Suggestion> suggestions = finder.find(messageRootTrie, tokens, descriptor);

        // Then
        assertThat(suggestions).hasSize(1);

        Suggestion suggestion = suggestions.iterator().next();
        assertThat(suggestion.getReturnTypeDisplayValue()).isEqualTo("List<TypesTestUtils$MapFirstType>");
        assertThat(suggestion.getType()).isEqualTo(Type.FUNCTION);
    }

    @Test
    void shouldReturnCorrectMessagePayloadTypeMultiples() {
        // Given
        ComponentOutputDescriptor descriptor = new ComponentOutputDescriptor();
        descriptor.setPayload(asList(MapFirstType.class.getName(), "byte[]", String.class.getName()));
        String[] tokens = new String[] {"message", "paylo"};

        // When
        Collection<Suggestion> suggestions = finder.find(messageRootTrie, tokens, descriptor);

        // Then
        assertThat(suggestions).hasSize(1);

        Suggestion suggestion = suggestions.iterator().next();
        assertThat(suggestion.getReturnTypeDisplayValue()).isEqualTo("TypesTestUtils$MapFirstType,byte[],String");
        assertThat(suggestion.getType()).isEqualTo(Type.FUNCTION);
    }

    // -------------------------------- Tests for --------------------------------
    // message.payload() (we check that there are the correct suggestions for the dynamic payload type)
    // ---------------------------------------------------------------------------
    @Test
    void shouldReturnCorrectSuggestionsForPayloadTypeMap() {
        // Given
        ComponentOutputDescriptor descriptor = new ComponentOutputDescriptor();
        descriptor.setPayload(singletonList(MapFirstType.class.getName()));
        String[] tokens = new String[] {"message", "payload", ""};

        // When
        Collection<Suggestion> suggestions = finder.find(messageRootTrie, tokens, descriptor);

        // Then
        assertThat(suggestions).hasSize(6);
        assertExistSuggestionWithName(suggestions, "find");
        assertExistSuggestionWithName(suggestions, "size");
        assertExistSuggestionWithName(suggestions, "each");
        assertExistSuggestionWithName(suggestions, "eachWithIndex");
        assertExistSuggestionWithName(suggestions, "firstProperty1");
        assertExistSuggestionWithName(suggestions, "firstProperty2");
    }

    @Test
    void shouldReturnCorrectSuggestionsForPayloadWithMultipleReturnValues() {
        // Given
        ComponentOutputDescriptor descriptor = new ComponentOutputDescriptor();
        descriptor.setPayload(asList(MapFirstType.class.getName(), MapSecondType.class.getName()));
        String[] tokens = new String[] {"message", "payload", ""};

        // When
        Collection<Suggestion> suggestions = finder.find(messageRootTrie, tokens, descriptor);

        // Then
        assertThat(suggestions).hasSize(8);
        assertExistSuggestionWithName(suggestions, "find");
        assertExistSuggestionWithName(suggestions, "size");
        assertExistSuggestionWithName(suggestions, "each");
        assertExistSuggestionWithName(suggestions, "eachWithIndex");
        assertExistSuggestionWithName(suggestions, "firstProperty1");
        assertExistSuggestionWithName(suggestions, "firstProperty2");
        assertExistSuggestionWithName(suggestions, "secondProperty1");
        assertExistSuggestionWithName(suggestions, "secondProperty2");
    }

    @Test
    void shouldReturnEmptySuggestionsWhenMessagePayloadAndNullOutputDescriptor() {
        // Given
        ComponentOutputDescriptor descriptor = null;
        String[] tokens = new String[] {"message", "payload", ""};

        // When
        Collection<Suggestion> suggestions = finder.find(messageRootTrie, tokens, descriptor);

        // Then
        assertThat(suggestions).isEmpty();
    }

    @Test
    void shouldReturnEmptySuggestionsWhenMessagePayloadAndNullPayload() {
        // Given
        ComponentOutputDescriptor descriptor = new ComponentOutputDescriptor();
        descriptor.setPayload(null);
        String[] tokens = new String[] {"message", "payload", ""};

        // When
        Collection<Suggestion> suggestions = finder.find(messageRootTrie, tokens, descriptor);

        // Then
        assertThat(suggestions).isEmpty();
    }

    @Test
    void shouldReturnEmptySuggestionsWhenMessagePayloadTypeIsUnknown() {
        // Given
        ComponentOutputDescriptor descriptor = new ComponentOutputDescriptor();
        descriptor.setPayload(singletonList(MyUnknownType.class.getName()));
        String[] tokens = new String[] {"message", "payload", ""};

        // When
        Collection<Suggestion> suggestions = finder.find(messageRootTrie, tokens, descriptor);

        // Then
        assertThat(suggestions).isEmpty();
    }

    // -------------------------------- Tests for --------------------------------
    // message.attributes() (we check that there are the correct suggestions for the dynamic attributes type)
    // ---------------------------------------------------------------------------
    @Test
    void shouldReturnCorrectSuggestionsForAttributeType() {
        // Given
        ComponentOutputDescriptor descriptor = new ComponentOutputDescriptor();
        descriptor.setAttributes(singletonList(MyAttributeType.class.getName()));
        String[] tokens = new String[] {"message", "attributes", ""};

        // When
        Collection<Suggestion> suggestions = finder.find(messageRootTrie, tokens, descriptor);

        // Then
        assertThat(suggestions).hasSize(3);
        assertExistSuggestionWithName(suggestions, "attributeProperty1");
        assertExistSuggestionWithName(suggestions, "attributeProperty2");
        assertExistSuggestionWithName(suggestions, "component");
    }

    @Test
    void shouldReturnCorrectDefaultSuggestionsForNullAttributeType() {
        // Given
        ComponentOutputDescriptor descriptor = new ComponentOutputDescriptor();
        descriptor.setAttributes(null);
        String[] tokens = new String[] {"message", "attributes", ""};

        // When
        Collection<Suggestion> suggestions = finder.find(messageRootTrie, tokens, descriptor);

        // Then
        assertThat(suggestions).hasSize(1);
        assertExistSuggestionWithName(suggestions, "component");
    }*/

    private void assertExistSuggestionWithName(Collection<Suggestion> suggestions, String expectedName) {
        boolean found = suggestions.stream().anyMatch(suggestion -> expectedName.equals(suggestion.getInsertValue()));
        assertThat(found)
                .withFailMessage("Could not find suggestion with expected name=<%s>", expectedName)
                .isTrue();
    }
}
