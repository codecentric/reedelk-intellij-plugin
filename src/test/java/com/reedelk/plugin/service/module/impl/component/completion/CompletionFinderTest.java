package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.runtime.api.message.Message;
import com.reedelk.runtime.api.message.MessagePayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class CompletionFinderTest {

    private TrieMapWrapper typeAndTrieMap;
    private Trie messageRootTrie;

    // TODO: Test where the output is null!

    private CompletionFinder completionFinder;


    @BeforeEach
    void setUp() {

        Map<String, Trie> tries = new HashMap<>();

        Trie messageType = new TrieDefault();
        messageType.insert(createFunction("payload", "payload", MessagePayload.class.getName()));
        tries.put(Message.class.getName(), messageType);

        Trie listMyItemType = new TrieDefault(ArrayList.class.getName(), MyItemType.class.getName());
        Trie myItemTypeTrie = new TrieDefault();
        myItemTypeTrie.insert(createFunction("method1", "method1", String.class.getName()));
        tries.put(ListMyItemType.class.getName(), listMyItemType);
        tries.put(MyItemType.class.getName(), myItemTypeTrie);


        Trie myMap1Type = new TrieDefault(HashMap.class.getName(), null);
        myMap1Type.insert(createProperty("property1", "property1", String.class.getName()));
        myMap1Type.insert(createProperty("property2", "property2", String.class.getName()));
        tries.put(MyMap1Type.class.getName(), myMap1Type);

        Trie listMyMap1Type = new TrieDefault(ArrayList.class.getName(), MyMap1Type.class.getName());
        tries.put(ListMyMap1Type.class.getName(), listMyMap1Type);

        Trie myMap2Type = new TrieDefault(HashMap.class.getName(), null);
        myMap2Type.insert(createProperty("secondProperty1", "secondProperty1", String.class.getName()));
        tries.put(MySecondMapType.class.getName(), myMap2Type);

        Trie genericMapTypeFunctions = new TrieDefault(null, null);
        genericMapTypeFunctions.insert(createFunction("size", "size", int.class.getName()));
        tries.put(HashMap.class.getName(), new TrieDefault(Map.class.getName(), null));
        tries.put(Map.class.getName(), genericMapTypeFunctions);
        typeAndTrieMap = new TrieMapWrapper(tries);


        Suggestion message = createProperty("message", "message", Message.class.getName());

        messageRootTrie = new TrieDefault();
        messageRootTrie.insert(message);

        completionFinder = new CompletionFinder(typeAndTrieMap);
    }

    @Test
    void shouldReturnPrimitiveDynamicMessagePayloadCorrectly() {
        // Given
        ComponentOutputDescriptor descriptor = new ComponentOutputDescriptor();
        descriptor.setPayload(Collections.singletonList(String.class.getName()));
        String[] tokens = new String[] {"message", "paylo"};

        // When
        Collection<Suggestion> suggestions = completionFinder.find(messageRootTrie, tokens, descriptor);

        // Then
        assertThat(suggestions).hasSize(1);
        Suggestion suggestion = suggestions.stream().findFirst().get();
        assertThat(suggestion.typeText()).isEqualTo(String.class.getName());
        assertThat(suggestion.getType()).isEqualTo(Suggestion.Type.FUNCTION);
    }

    @Test
    void shouldReturnListWithObjectDynamicMessagePayloadCorrectly() {
        // Given
        ComponentOutputDescriptor descriptor = new ComponentOutputDescriptor();
        descriptor.setPayload(Collections.singletonList(ListMyItemType.class.getName()));
        String[] tokens = new String[] {"message", "paylo"};

        // When
        Collection<Suggestion> suggestions = completionFinder.find(messageRootTrie, tokens, descriptor);

        // Then
        assertThat(suggestions).hasSize(1);
        Suggestion suggestion = suggestions.stream().findFirst().get();
        assertThat(suggestion.presentableType()).isEqualTo("List<MyItemType>");
        assertThat(suggestion.getType()).isEqualTo(Suggestion.Type.FUNCTION);
    }

    @Test
    void shouldReturnListWithMapDynamicMessagePayloadCorrectly() {
        // Given
        ComponentOutputDescriptor descriptor = new ComponentOutputDescriptor();
        descriptor.setPayload(Collections.singletonList(ListMyMap1Type.class.getName()));
        String[] tokens = new String[] {"message", "paylo"};

        // When
        Collection<Suggestion> suggestions = completionFinder.find(messageRootTrie, tokens, descriptor);

        // Then
        assertThat(suggestions).hasSize(1);
        Suggestion suggestion = suggestions.stream().findFirst().get();
        assertThat(suggestion.presentableType()).isEqualTo("List<MyMap1Type>");
        assertThat(suggestion.getType()).isEqualTo(Suggestion.Type.FUNCTION);
    }

    @Test
    void shouldReturnMapDynamicMessagePayloadCorrectly() {
        // Given
        ComponentOutputDescriptor descriptor = new ComponentOutputDescriptor();
        descriptor.setPayload(Collections.singletonList(MyMap1Type.class.getName()));
        String[] tokens = new String[] {"message", "paylo"};

        // When
        Collection<Suggestion> suggestions = completionFinder.find(messageRootTrie, tokens, descriptor);

        // Then
        assertThat(suggestions).hasSize(1);
        Suggestion suggestion = suggestions.stream().findFirst().get();
        assertThat(suggestion.presentableType()).isEqualTo("MyMap1Type");
        assertThat(suggestion.getType()).isEqualTo(Suggestion.Type.FUNCTION);
    }

    @Test
    void shouldAutocompleteReturnMapDynamicMessagePayloadCorrectly() {
        // Given
        ComponentOutputDescriptor descriptor = new ComponentOutputDescriptor();
        descriptor.setPayload(Collections.singletonList(MyMap1Type.class.getName()));
        String[] tokens = new String[] {"message", "payload", ""};

        // When
        Collection<Suggestion> suggestions = completionFinder.find(messageRootTrie, tokens, descriptor);

        // Then
        assertThat(suggestions).hasSize(3);
        assertExistSuggestionWithName(suggestions, "property1");
        assertExistSuggestionWithName(suggestions, "property2");
        assertExistSuggestionWithName(suggestions, "size");
    }

    @Test
    void shouldReturnSuggestionsForMultipleReturnTypes() {
        // Given
        ComponentOutputDescriptor descriptor = new ComponentOutputDescriptor();
        descriptor.setPayload(Arrays.asList(MyMap1Type.class.getName(), MySecondMapType.class.getName()));
        String[] tokens = new String[] {"message", "payload", ""};

        // When
        Collection<Suggestion> suggestions = completionFinder.find(messageRootTrie, tokens, descriptor);

        // Then
        assertThat(suggestions).hasSize(4);
        assertExistSuggestionWithName(suggestions, "property1");
        assertExistSuggestionWithName(suggestions, "property2");
        assertExistSuggestionWithName(suggestions, "secondProperty1");
        assertExistSuggestionWithName(suggestions, "size");
    }


    private void assertExistSuggestionWithName(Collection<Suggestion> suggestions, String expectedName) {
        boolean found = suggestions.stream().anyMatch(suggestion -> expectedName.equals(suggestion.name()));
        assertThat(found).withFailMessage("Could not find suggestion with expected name=<%s>", expectedName).isTrue();
    }

    private static Suggestion createFunction(String lookup, String name, String type) {
        return Suggestion.create(Suggestion.Type.FUNCTION)
                .withLookupString(lookup)
                .withName(name)
                .withType(type)
                .build();
    }

    private static Suggestion createProperty(String lookup, String name, String type) {
        return Suggestion.create(Suggestion.Type.PROPERTY)
                .withLookupString(lookup)
                .withName(name)
                .withType(type)
                .build();
    }
}
