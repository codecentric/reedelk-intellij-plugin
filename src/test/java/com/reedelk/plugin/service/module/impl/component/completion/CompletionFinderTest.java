package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutput;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutputDefault;
import com.reedelk.runtime.api.message.Message;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.reedelk.plugin.service.module.impl.component.completion.SuggestionTestUtils.createPropertySuggestion;
import static com.reedelk.plugin.service.module.impl.component.completion.TypesTestUtils.MapFirstType;
import static com.reedelk.plugin.service.module.impl.component.completion.TypesTestUtils.TEST_TYPES;
import static com.reedelk.runtime.api.commons.StringUtils.EMPTY;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class CompletionFinderTest {

    private static Trie messageRootTrie;
    private static CompletionFinder finder;
    private static TypeAndTries typeAndTries;

    @BeforeAll
    static void setUp() {
        Map<String, Trie> defaultTypesAndTries = new HashMap<>();
        TEST_TYPES.forEach(trieProvider -> trieProvider.register(defaultTypesAndTries));
        typeAndTries = new TypeAndTries(defaultTypesAndTries);

        Suggestion message = createPropertySuggestion("message", Message.class.getName());
        messageRootTrie = new TrieDefault();
        messageRootTrie.insert(message);

        finder = new CompletionFinder(typeAndTries);
    }

    // -------------------------------- Tests for --------------------------------
    // message.paylo (we check the dynamic return type)
    // ---------------------------------------------------------------------------
    @Test
    void shouldReturnCorrectMessagePayloadTypeString() {
        // Given
        List<String> attributes = emptyList();
        List<String> payload = singletonList(String.class.getName());
        PreviousComponentOutput previousOutput = new PreviousComponentOutputDefault(attributes, payload, EMPTY);
        String[] tokens = new String[] {"message", "paylo"};

        // When
        Collection<Suggestion> suggestions = finder.find(messageRootTrie, tokens, previousOutput);

        // Then
        assertThat(suggestions).hasSize(1);

        Suggestion suggestion = suggestions.iterator().next();
        assertThat(suggestion.getReturnType().getTypeFullyQualifiedName()).isEqualTo(String.class.getName());
        assertThat(suggestion.getReturnTypeDisplayValue()).isEqualTo(String.class.getSimpleName());
        assertThat(suggestion.getType()).isEqualTo(Type.FUNCTION);
    }

    @Test
    void shouldReturnCorrectMessagePayloadTypeMap() {
        // Given
        List<String> attributes = emptyList();
        List<String> payload = singletonList(MapFirstType.class.getName());
        PreviousComponentOutput previousOutput = new PreviousComponentOutputDefault(attributes, payload, EMPTY);
        String[] tokens = new String[] {"message", "paylo"};

        // When
        Collection<Suggestion> suggestions = finder.find(messageRootTrie, tokens, previousOutput);

        // Then
        assertThat(suggestions).hasSize(1);

        Suggestion suggestion = suggestions.iterator().next();
        assertThat(suggestion.getReturnTypeDisplayValue()).isEqualTo(MapFirstType.class.getSimpleName());
        assertThat(suggestion.getType()).isEqualTo(Type.FUNCTION);
    }

    @Test
    void shouldReturnCorrectMessagePayloadTypeMapEachAnd() {
        // Given
        List<String> attributes = emptyList();
        List<String> payload = singletonList(MapFirstType.class.getName());
        PreviousComponentOutput previousOutput = new PreviousComponentOutputDefault(attributes, payload, EMPTY);
        String[] tokens = new String[] {"message", "payload", "each", "{", ""};

        // When
        Collection<Suggestion> suggestions = finder.find(messageRootTrie, tokens, previousOutput);

        // Then
        assertThat(suggestions).hasSize(1);

        Suggestion suggestion = suggestions.iterator().next();
        assertThat(suggestion.getReturnTypeDisplayValue()).isEqualTo(MapFirstType.class.getSimpleName());
        assertThat(suggestion.getType()).isEqualTo(Type.FUNCTION);
    }

    /**
    @Test
    void shouldReturnCorrectMessagePayloadTypeListOfSomeKnownType() {
        // Given
        ComponentOutputDescriptor descriptor = new ComponentOutputDescriptor();
        descriptor.setPayload(singletonList(ListMyItemType.class.getName()));
        String[] tokens = new String[] {"message", "paylo"};

        // When
        Collection<Suggestion> suggestions = finder.find(messageRootTrie, tokens, descriptor);

        // Then
        assertThat(suggestions).hasSize(1);
        Suggestion suggestion = suggestions.iterator().next();
        assertThat(suggestion.getReturnTypeDisplayValue()).isEqualTo("List<TypesTestUtils$MyItemType>");
        assertThat(suggestion.getType()).isEqualTo(Type.FUNCTION);
    }

    // A type which was not registered in the type map tree structure.
    @Test
    void shouldReturnCorrectMessagePayloadTypeListOfSomeUnknownType() {
        // Given
        ComponentOutputDescriptor descriptor = new ComponentOutputDescriptor();
        descriptor.setPayload(singletonList(ListMyUnknownType.class.getName()));
        String[] tokens = new String[] {"message", "paylo"};

        // When
        Collection<Suggestion> suggestions = finder.find(messageRootTrie, tokens, descriptor);

        // Then
        assertThat(suggestions).hasSize(1);
        Suggestion suggestion = suggestions.iterator().next();
        assertThat(suggestion.getReturnTypeDisplayValue()).isEqualTo("List<TypesTestUtils$MyUnknownType>");
        assertThat(suggestion.getType()).isEqualTo(Type.FUNCTION);
    }

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
