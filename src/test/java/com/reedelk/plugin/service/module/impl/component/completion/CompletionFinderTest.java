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

    @BeforeEach
    void setUp() {

        Trie messageTypeTrie = new TrieDefault();
        Suggestion payload = Suggestion.create(Suggestion.Type.FUNCTION)
                .withLookupString("payload")
                .withName("payload")
                .withType(MessagePayload.class.getName())
                .build();
        messageTypeTrie.insert(payload);

        Trie listObjectType = new TrieDefault(ArrayList.class.getName(), MyItemType.class.getName());

        Trie myItemTypeTrie = new TrieDefault();
        Suggestion myItemMethod1 = Suggestion.create(Suggestion.Type.FUNCTION)
                .withLookupString("method1")
                .withName("method1")
                .withType(String.class.getName())
                .build();
        myItemTypeTrie.insert(myItemMethod1);

        Trie listMapType = new TrieDefault(ArrayList.class.getName(), MyMapType.class.getName());

        Trie myMapType = new TrieDefault(HashMap.class.getName(), null);
        Suggestion myMapProperty1 = Suggestion.create(Suggestion.Type.PROPERTY)
                .withLookupString("property1")
                .withName("property1")
                .withType(String.class.getName())
                .build();
        myMapType.insert(myMapProperty1);
        Suggestion myMapProperty2 = Suggestion.create(Suggestion.Type.PROPERTY)
                .withLookupString("property2")
                .withName("property2")
                .withType(String.class.getName())
                .build();
        myMapType.insert(myMapProperty2);


        Trie mySecondMapType = new TrieDefault(HashMap.class.getName(), null);
        Suggestion mySecondMapProperty2 = Suggestion.create(Suggestion.Type.PROPERTY)
                .withLookupString("secondProperty1")
                .withName("secondProperty1")
                .withType(String.class.getName())
                .build();
        mySecondMapType.insert(mySecondMapProperty2);


        Trie hashMapType = new TrieDefault(Map.class.getName(), null);

        Trie mapType = new TrieDefault(null, null);
        mapType.insert(Suggestion.create(Suggestion.Type.FUNCTION)
                .withName("size")
                .withLookupString("size")
                .withType(int.class.getName())
                .build());

        Map<String, Trie> tries = new HashMap<>();
        tries.put(Message.class.getName(), messageTypeTrie);
        tries.put(MyListObjectType.class.getName(), listObjectType);
        tries.put(MyItemType.class.getName(), myItemTypeTrie);
        tries.put(MyListMapType.class.getName(), listMapType);
        tries.put(MyMapType.class.getName(), myMapType);
        tries.put(MySecondMapType.class.getName(), mySecondMapType);
        tries.put(HashMap.class.getName(), hashMapType);
        tries.put(Map.class.getName(), mapType);
        typeAndTrieMap = new TrieMapWrapper(tries);



        Suggestion message = Suggestion.create(Suggestion.Type.PROPERTY)
                .withLookupString("message")
                .withName("message")
                .withType(Message.class.getName())
                .build();

        messageRootTrie = new TrieDefault();
        messageRootTrie.insert(message);
    }

    @Test
    void shouldReturnPrimitiveDynamicMessagePayloadCorrectly() {
        // Given
        ComponentOutputDescriptor descriptor = new ComponentOutputDescriptor();
        descriptor.setPayload(Collections.singletonList(String.class.getName()));
        String[] tokens = new String[] {"message", "paylo"};

        // When
        Collection<Suggestion> suggestions = CompletionFinder.find(messageRootTrie, typeAndTrieMap, descriptor, tokens);

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
        descriptor.setPayload(Collections.singletonList(MyListObjectType.class.getName()));
        String[] tokens = new String[] {"message", "paylo"};

        // When
        Collection<Suggestion> suggestions = CompletionFinder.find(messageRootTrie, typeAndTrieMap, descriptor, tokens);

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
        descriptor.setPayload(Collections.singletonList(MyListMapType.class.getName()));
        String[] tokens = new String[] {"message", "paylo"};

        // When
        Collection<Suggestion> suggestions = CompletionFinder.find(messageRootTrie, typeAndTrieMap, descriptor, tokens);

        // Then
        assertThat(suggestions).hasSize(1);
        Suggestion suggestion = suggestions.stream().findFirst().get();
        assertThat(suggestion.presentableType()).isEqualTo("List<MyMapType>");
        assertThat(suggestion.getType()).isEqualTo(Suggestion.Type.FUNCTION);
    }

    @Test
    void shouldReturnMapDynamicMessagePayloadCorrectly() {
        // Given
        ComponentOutputDescriptor descriptor = new ComponentOutputDescriptor();
        descriptor.setPayload(Collections.singletonList(MyMapType.class.getName()));
        String[] tokens = new String[] {"message", "paylo"};

        // When
        Collection<Suggestion> suggestions = CompletionFinder.find(messageRootTrie, typeAndTrieMap, descriptor, tokens);

        // Then
        assertThat(suggestions).hasSize(1);
        Suggestion suggestion = suggestions.stream().findFirst().get();
        assertThat(suggestion.presentableType()).isEqualTo("MyMapType");
        assertThat(suggestion.getType()).isEqualTo(Suggestion.Type.FUNCTION);
    }

    @Test
    void shouldAutocompleteReturnMapDynamicMessagePayloadCorrectly() {
        // Given
        ComponentOutputDescriptor descriptor = new ComponentOutputDescriptor();
        descriptor.setPayload(Collections.singletonList(MyMapType.class.getName()));
        String[] tokens = new String[] {"message", "payload", ""};

        // When
        Collection<Suggestion> suggestions = CompletionFinder.find(messageRootTrie, typeAndTrieMap, descriptor, tokens);

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
        descriptor.setPayload(Arrays.asList(MyMapType.class.getName(), MySecondMapType.class.getName()));
        String[] tokens = new String[] {"message", "payload", ""};

        // When
        Collection<Suggestion> suggestions = CompletionFinder.find(messageRootTrie, typeAndTrieMap, descriptor, tokens);

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
}
