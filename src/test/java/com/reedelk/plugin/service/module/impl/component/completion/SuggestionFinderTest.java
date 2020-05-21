package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutput;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutputDefault;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.*;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.FUNCTION;
import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;
import static com.reedelk.plugin.service.module.impl.component.completion.TypeTestUtils.*;
import static com.reedelk.runtime.api.commons.StringUtils.EMPTY;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

class SuggestionFinderTest {

    private static Trie MESSAGE_AND_CONTEXT_TRIE = TypeDefault.MESSAGE_AND_CONTEXT;
    private static SuggestionFinder FINDER;

    @BeforeAll
    static void setUp() {
        Map<String, Trie> defaultTypesAndTries = new HashMap<>();
        TypeAndTries typeAndTries = new TypeAndTries(defaultTypesAndTries);
        ALL_TYPES.forEach(trieProvider -> trieProvider.register(typeAndTries, defaultTypesAndTries));
        FINDER = new SuggestionFinder(typeAndTries);
    }

    // Verify dynamic return type computed correctly.
    @Test
    void shouldReturnCorrectSuggestionsForTypeString() {
        // Given
        PreviousComponentOutput output = createOutput(String.class);
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
        PreviousComponentOutput output = createOutput(MapFirstType.class);
        String[] tokens = new String[] {"message", "paylo"};

        // When
        Collection<Suggestion> suggestions = FINDER.suggest(MESSAGE_AND_CONTEXT_TRIE, tokens, output);

        // Then
        PluginAssertion.assertThat(suggestions).hasSize(1)
                .contains(FUNCTION,
                        "payload",
                        "payload",
                        MapFirstType.class.getName(),
                        "Map<String,Serializable>");
    }

    @Test
    void shouldReturnCorrectSuggestionsForClosure() {
        // Given
        PreviousComponentOutput output = createOutput(MapFirstType.class);
        String[] tokens = new String[] {"message", "payload", "each", "{", ""};

        // When
        Collection<Suggestion> suggestions = FINDER.suggest(MESSAGE_AND_CONTEXT_TRIE, tokens, output);

        // Then the closure has two properties: 'i' and 'each' to iterate the map content. 'i' and 'each' must
        // have the type of the 'MapFirstType' (<String, Serializable>).
        PluginAssertion.assertThat(suggestions)
                .hasSize(3)
                .contains(PROPERTY,
                        "i",
                        "i",
                        String.class.getName(),
                        String.class.getSimpleName())
                .contains(PROPERTY,
                        "entry",
                        "entry",
                        Serializable.class.getName(),
                        Serializable.class.getSimpleName());
    }

    @Test
    void shouldReturnCorrectMessagePayloadTypeListOfSomeKnownType() {
        // Given
        PreviousComponentOutput output = createOutput(ListMyItemType.class);
        String[] tokens = new String[] {"message", "paylo"};

        // When
        Collection<Suggestion> suggestions = FINDER.suggest(MESSAGE_AND_CONTEXT_TRIE, tokens, output);

        // Then
        PluginAssertion.assertThat(suggestions).hasSize(1)
                .contains(FUNCTION,
                        "payload",
                        "payload",
                        ListMyItemType.class.getName(), "List<TypeTestUtils$MyItemType>");
    }

    // A type which was not registered in the type map tree structure.
    @Test
    void shouldReturnCorrectMessagePayloadTypeListOfSomeUnknownType() {
        // Given
        PreviousComponentOutput descriptor = createOutput(ListMyUnknownType.class);
        String[] tokens = new String[] {"message", "paylo"};

        // When
        Collection<Suggestion> suggestions = FINDER.suggest(MESSAGE_AND_CONTEXT_TRIE, tokens, descriptor);

        // Then
        PluginAssertion.assertThat(suggestions).hasSize(1)
                .contains(FUNCTION,
                        "payload",
                        "payload",
                        ListMyUnknownType.class.getName(),
                        "List<TypeTestUtils$MyUnknownType>");
    }

    @Test
    void shouldReturnToStringForListType() {
        // Given
        PreviousComponentOutput descriptor = createOutput(ListMyUnknownType.class);
        String[] tokens = new String[] {"message", "payload", ""};

        // When
        Collection<Suggestion> suggestions = FINDER.suggest(MESSAGE_AND_CONTEXT_TRIE, tokens, descriptor);

        // Then
        PluginAssertion.assertThat(suggestions)
                .contains(FUNCTION,
                        "toString()",
                        "toString",
                        String.class.getName(),
                        String.class.getSimpleName());
    }

    @Test
    void shouldReturnClosureSuggestionsForListType() {
        // Given
        PreviousComponentOutput descriptor = createOutput(ListMapFirstType.class);
        String[] tokens = new String[] {"message", "payload", ""};

        // When
        Collection<Suggestion> suggestions = FINDER.suggest(MESSAGE_AND_CONTEXT_TRIE, tokens, descriptor);

        // Then
        PluginAssertion.assertThat(suggestions)
                .contains(FUNCTION,
                        "eachWithIndex { it, i ->  }",
                        "eachWithIndex",
                        ListMapFirstType.class.getName(),
                        "List<Map<String,Serializable>>");

        PluginAssertion.assertThat(suggestions)
                .contains(FUNCTION,
                        "each { it }",
                        "each",
                        ListMapFirstType.class.getName(),
                        "List<Map<String,Serializable>>");

        PluginAssertion.assertThat(suggestions)
                .contains(FUNCTION,
                        "collect { it }",
                        "collect",
                        ListMapFirstType.class.getName(),
                        "List<Map<String,Serializable>>");
    }

    @Test
    void shouldCorrectlyFlattenReturnTypesWhenMultiple() {
        // Given
        PreviousComponentOutput output = createOutput(MapFirstType.class, Integer.class, String.class);
        String[] tokens = new String[] {"message", "paylo"};

        // When
        Collection<Suggestion> suggestions = FINDER.suggest(MESSAGE_AND_CONTEXT_TRIE, tokens, output);

        // Then
        PluginAssertion.assertThat(suggestions).hasSize(1)
                .contains(FUNCTION,
                        "payload",
                        "payload",
                        FlattenedReturnType.class.getName(),
                        "Map<String,Serializable>,Integer,String");
    }

    // Multiple output suggestions not flattened.
    @Test
    void shouldReturnCorrectSuggestionsForPayloadTypeMap() {
        // Given
        PreviousComponentOutput output = createOutput(MapFirstType.class, MapSecondType.class, String.class);
        String[] tokens = new String[] {"message", "payload", ""};

        // When
        Collection<Suggestion> suggestions = FINDER.suggest(MESSAGE_AND_CONTEXT_TRIE, tokens, output);

        // Then
        assertThat(suggestions).hasSize(7);

        PluginAssertion.assertThat(suggestions)
                .contains(PROPERTY,
                        "firstProperty1",
                        "firstProperty1",
                        String.class.getName(),
                        String.class.getSimpleName());

        PluginAssertion.assertThat(suggestions)
                .contains(PROPERTY,
                        "firstProperty2",
                        "firstProperty2",
                        String.class.getName(),
                        String.class.getSimpleName());

        PluginAssertion.assertThat(suggestions)
                .contains(PROPERTY,
                        "secondProperty1",
                        "secondProperty1",
                        String.class.getName(),
                        String.class.getSimpleName());

        PluginAssertion.assertThat(suggestions)
                .contains(PROPERTY,
                        "secondProperty2",
                        "secondProperty2",
                        long.class.getName(),
                        long.class.getSimpleName());

        PluginAssertion.assertThat(suggestions)
                .contains(FUNCTION,
                        "toString()",
                        "toString",
                        String.class.getName(),
                        String.class.getSimpleName());
    }

    /**
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

private PreviousComponentOutput createOutput(Class<?> ...payloadType) {
    List<String> attributes = emptyList();
    List<String> payload = Arrays.stream(payloadType).map(Class::getName).collect(toList());
    return new PreviousComponentOutputDefault(attributes, payload, EMPTY);
}

    private void assertExistSuggestionWithName(Collection<Suggestion> suggestions, String expectedName) {
        boolean found = suggestions.stream().anyMatch(suggestion -> expectedName.equals(suggestion.getInsertValue()));
        assertThat(found)
                .withFailMessage("Could not find suggestion with expected name=<%s>", expectedName)
                .isTrue();
    }
}
