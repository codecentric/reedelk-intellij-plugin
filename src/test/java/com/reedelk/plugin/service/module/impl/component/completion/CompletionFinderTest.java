package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.runtime.api.message.Message;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type;
import static com.reedelk.plugin.service.module.impl.component.completion.SuggestionTestUtils.createProperty;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class CompletionFinderTest {

    private static Trie messageRootTrie;
    private static CompletionFinder finder;

    @BeforeAll
    static void setUp() {
        Map<String, Trie> tries = new HashMap<>();

        // Object Types
        MessageType.initialize(tries);
        MyItemType.initialize(tries);

        // Map Types
        GenericMapTypeFunctions.initialize(tries);
        MapFirstType.initialize(tries);
        MapSecondType.initialize(tries);

        // List Types
        ListMyItemType.initialize(tries);
        ListMapFirstType.initialize(tries);
        ListMyUnknownType.initialize(tries);

        Suggestion message = createProperty("message", "message", Message.class.getName());
        messageRootTrie = new TrieDefault();
        messageRootTrie.insert(message);

        finder = new CompletionFinder(new TrieMapWrapper(tries));
    }

    // -------------------------------- Tests for --------------------------------
    // message.paylo (we check the dynamic return type)
    // ---------------------------------------------------------------------------
    @Test
    void shouldReturnCorrectMessagePayloadTypeString() {
        // Given
        ComponentOutputDescriptor descriptor = new ComponentOutputDescriptor();
        descriptor.setPayload(singletonList(String.class.getName()));
        String[] tokens = new String[] {"message", "paylo"};

        // When
        Collection<Suggestion> suggestions = finder.find(messageRootTrie, tokens, descriptor);

        // Then
        assertThat(suggestions).hasSize(1);

        Suggestion suggestion = suggestions.iterator().next();
        assertThat(suggestion.typeText()).isEqualTo(String.class.getName());
        assertThat(suggestion.getType()).isEqualTo(Type.FUNCTION);
    }

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
        assertThat(suggestion.presentableType()).isEqualTo("List<MyItemType>");
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
        assertThat(suggestion.presentableType()).isEqualTo("List<MyUnknownType>");
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
        assertThat(suggestion.presentableType()).isEqualTo("List<MapFirstType>");
        assertThat(suggestion.getType()).isEqualTo(Type.FUNCTION);
    }

    @Test
    void shouldReturnCorrectMessagePayloadTypeMap() {
        // Given
        ComponentOutputDescriptor descriptor = new ComponentOutputDescriptor();
        descriptor.setPayload(singletonList(MapFirstType.class.getName()));
        String[] tokens = new String[] {"message", "paylo"};

        // When
        Collection<Suggestion> suggestions = finder.find(messageRootTrie, tokens, descriptor);

        // Then
        assertThat(suggestions).hasSize(1);

        Suggestion suggestion = suggestions.iterator().next();
        assertThat(suggestion.presentableType()).isEqualTo("MapFirstType");
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
        assertThat(suggestion.presentableType()).isEqualTo("MapFirstType,byte[],String");
        assertThat(suggestion.getType()).isEqualTo(Type.FUNCTION);
    }

    // -------------------------------- Tests for --------------------------------
    // message.payload() (we check that there are the correct suggestions)
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
    void shouldReturnEmptySuggestionsWhenMessagePayloadAnOutputDescriptorPayload() {
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

    private void assertExistSuggestionWithName(Collection<Suggestion> suggestions, String expectedName) {
        boolean found = suggestions.stream().anyMatch(suggestion -> expectedName.equals(suggestion.name()));
        assertThat(found)
                .withFailMessage("Could not find suggestion with expected name=<%s>", expectedName)
                .isTrue();
    }
}
