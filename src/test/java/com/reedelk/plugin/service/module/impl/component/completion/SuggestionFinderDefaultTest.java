package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutput;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutputDefault;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.FUNCTION;
import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;
import static com.reedelk.plugin.service.module.impl.component.completion.TypeTestUtils.*;
import static com.reedelk.runtime.api.commons.StringUtils.EMPTY;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

class SuggestionFinderDefaultTest extends AbstractCompletionTest {

    private Trie messageAndContextTrie = TypeBuiltIn.MESSAGE_AND_CONTEXT;
    private SuggestionFinderDefault finder;

    @BeforeEach
    public void setUp() {
        finder = new SuggestionFinderDefault(typeAndTries);
    }

    // Verify dynamic return type computed correctly.
    @Test
    void shouldReturnCorrectSuggestionsForTypeString() {
        // Given
        PreviousComponentOutput output = createOutput(String.class);
        String[] tokens = new String[]{"message", "paylo"};

        // When
        Collection<Suggestion> suggestions = finder.suggest(messageAndContextTrie, tokens, output);

        // Then
        PluginAssertion.assertThat(suggestions).hasSize(1)
                .contains(FUNCTION, "payload", "payload", String.class.getName(), String.class.getSimpleName());
    }

    @Test
    void shouldReturnCorrectSuggestionsForTypeMap() {
        // Given
        PreviousComponentOutput output = createOutput(MapFirstType.class);
        String[] tokens = new String[]{"message", "paylo"};

        // When
        Collection<Suggestion> suggestions = finder.suggest(messageAndContextTrie, tokens, output);

        // Then
        PluginAssertion.assertThat(suggestions).hasSize(1)
                .contains(FUNCTION,
                        "payload",
                        "payload",
                        MapFirstType.class.getName(),
                        "MapFirstType");
    }

    @Test
    void shouldReturnCorrectSuggestionsForTypeMapWithoutDisplayName() {
        // Given
        PreviousComponentOutput output = createOutput(MapSecondType.class);
        String[] tokens = new String[]{"message", "paylo"};

        // When
        Collection<Suggestion> suggestions = finder.suggest(messageAndContextTrie, tokens, output);

        // Then
        PluginAssertion.assertThat(suggestions).hasSize(1)
                .contains(FUNCTION,
                        "payload",
                        "payload",
                        MapSecondType.class.getName(),
                        "Map<String,Serializable>");
    }

    @Test
    void shouldReturnCorrectSuggestionsForClosure() {
        // Given
        PreviousComponentOutput output = createOutput(MapFirstType.class);
        String[] tokens = new String[]{"message", "payload", "each", "{", ""};

        // When
        Collection<Suggestion> suggestions = finder.suggest(messageAndContextTrie, tokens, output);

        // Then the closure has two properties: 'i' and 'each' to iterate the map content. 'i' and 'each' must
        // have the type of the 'MapFirstType' (<String, Serializable>).
        PluginAssertion.assertThat(suggestions)
                .hasSize(2)
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
        String[] tokens = new String[]{"message", "paylo"};

        // When
        Collection<Suggestion> suggestions = finder.suggest(messageAndContextTrie, tokens, output);

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
        String[] tokens = new String[]{"message", "paylo"};

        // When
        Collection<Suggestion> suggestions = finder.suggest(messageAndContextTrie, tokens, descriptor);

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
        String[] tokens = new String[]{"message", "payload", ""};

        // When
        Collection<Suggestion> suggestions = finder.suggest(messageAndContextTrie, tokens, descriptor);

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
        String[] tokens = new String[]{"message", "payload", ""};

        // When
        Collection<Suggestion> suggestions = finder.suggest(messageAndContextTrie, tokens, descriptor);

        // Then
        PluginAssertion.assertThat(suggestions)
                .contains(FUNCTION,
                        "eachWithIndex { it, i ->  }",
                        "eachWithIndex",
                        ListMapFirstType.class.getName(),
                        "List<MapFirstType>")
                .contains(FUNCTION,
                        "each { it }",
                        "each",
                        ListMapFirstType.class.getName(),
                        "List<MapFirstType>")
                .contains(FUNCTION,
                        "collect { it }",
                        "collect",
                        List.class.getName(),
                        "List<Object>");
    }

    @Test
    void shouldCorrectlyFlattenReturnTypesWhenMultiple() {
        // Given
        PreviousComponentOutput output = createOutput(MapFirstType.class, Integer.class, String.class);
        String[] tokens = new String[]{"message", "paylo"};

        // When
        Collection<Suggestion> suggestions = finder.suggest(messageAndContextTrie, tokens, output);

        // Then
        PluginAssertion.assertThat(suggestions).hasSize(1)
                .contains(FUNCTION,
                        "payload",
                        "payload",
                        String.join(",", MapFirstType.class.getName(), Integer.class.getName(), String.class.getName()),
                        "MapFirstType,Integer,String");
    }

    // Multiple output suggestions not flattened.
    @Test
    void shouldReturnCorrectSuggestionsForPayloadTypeMap() {
        // Given
        PreviousComponentOutput output = createOutput(MapFirstType.class, MapSecondType.class, String.class);
        String[] tokens = new String[]{"message", "payload", ""};

        // When
        Collection<Suggestion> suggestions = finder.suggest(messageAndContextTrie, tokens, output);

        // Then
        PluginAssertion.assertThat(suggestions).hasSize(10)
                .contains(PROPERTY,
                        "firstProperty1",
                        "firstProperty1",
                        String.class.getName(),
                        String.class.getSimpleName())
                .contains(PROPERTY,
                        "firstProperty2",
                        "firstProperty2",
                        String.class.getName(),
                        String.class.getSimpleName())
                .contains(PROPERTY,
                        "secondProperty1",
                        "secondProperty1",
                        String.class.getName(),
                        String.class.getSimpleName())
                .contains(PROPERTY,
                        "secondProperty2",
                        "secondProperty2",
                        long.class.getName(),
                        long.class.getSimpleName())
                .contains(FUNCTION,
                        "toString()",
                        "toString",
                        String.class.getName(),
                        String.class.getSimpleName())
                // Make sure that the two lambda aware functions are flattened
                .contains(FUNCTION,
                        "eachWithIndex { entry, i ->  }",
                        "eachWithIndex",
                        MapFirstType.class.getName() + "," + MapSecondType.class.getName(),
                        "MapFirstType,Map<String,Serializable>")
                .contains(FUNCTION,
                        "each { entry }",
                        "each",
                        MapFirstType.class.getName() + "," + MapSecondType.class.getName(),
                        "MapFirstType,Map<String,Serializable>");
    }

    @Test
    void shouldReturnOnlyToStringWhenPreviousOutputIsNull() {
        // Given
        String[] tokens = new String[]{"message", "payload", ""};

        // When
        Collection<Suggestion> suggestions = finder.suggest(messageAndContextTrie, tokens, null);

        // Then
        PluginAssertion.assertThat(suggestions)
                .contains(FUNCTION,
                        "toString()",
                        "toString",
                        String.class.getName(),
                        String.class.getSimpleName());
    }

    @Test
    void shouldReturnOnlyToStringWhenPreviousOutputHasEmptyPayloadTypes() {
        // Given
        PreviousComponentOutput output = createOutput(MapFirstType.class, MapSecondType.class, String.class);
        String[] tokens = new String[]{"message", "payload", ""};

        // When
        Collection<Suggestion> suggestions = finder.suggest(messageAndContextTrie, tokens, output);

        // Then
        PluginAssertion.assertThat(suggestions)
                .contains(FUNCTION,
                        "toString()",
                        "toString",
                        String.class.getName(),
                        String.class.getSimpleName());
    }

    // MyUnknown type is not registered.
    @Test
    void shouldReturnOnlyToStringWhenPreviousOutputUnknownType() {
        // Given
        PreviousComponentOutput output = createOutput(MyUnknownType.class);
        String[] tokens = new String[]{"message", "payload", ""};

        // When
        Collection<Suggestion> suggestions = finder.suggest(messageAndContextTrie, tokens, output);

        // Then
        PluginAssertion.assertThat(suggestions)
                .contains(FUNCTION,
                        "toString()",
                        "toString",
                        String.class.getName(),
                        String.class.getSimpleName());
    }


    // -------------------------------- Tests for --------------------------------
    // message.attributes() (we check that there are the correct suggestions for the dynamic attributes type)
    // ---------------------------------------------------------------------------
    @Test
    void shouldReturnCorrectSuggestionsForAttributeType() {
        // Given
        PreviousComponentOutput output =
                new PreviousComponentOutputDefault(singletonList(MyAttributeType.class.getName()), emptyList(), EMPTY);
        String[] tokens = new String[]{"message", "attributes", ""};

        // When
        Collection<Suggestion> suggestions = finder.suggest(messageAndContextTrie, tokens, output);

        // Then
        PluginAssertion.assertThat(suggestions).hasSize(4)
                .contains(PROPERTY,
                        "attributeProperty1",
                        "attributeProperty1",
                        String.class.getName(),
                        String.class.getSimpleName())
                .contains(PROPERTY,
                        "attributeProperty2",
                        "attributeProperty2",
                        long.class.getName(),
                        long.class.getSimpleName())
                .contains(PROPERTY,
                        "component",
                        "component",
                        String.class.getName(),
                        String.class.getSimpleName())
                .contains(FUNCTION,
                        "toString()",
                        "toString",
                        String.class.getName(),
                        String.class.getSimpleName());
    }

    @Test
    void shouldReturnCorrectDefaultSuggestionsForEmptyAttributeType() {
        // Given
        PreviousComponentOutput output =
                new PreviousComponentOutputDefault(emptyList(), emptyList(), EMPTY);
        String[] tokens = new String[]{"message", "attributes", ""};

        // When
        Collection<Suggestion> suggestions = finder.suggest(messageAndContextTrie, tokens, output);

        // Then
        PluginAssertion.assertThat(suggestions)
                .contains(PROPERTY,
                        "component",
                        "component",
                        String.class.getName(),
                        String.class.getSimpleName())
                .contains(FUNCTION,
                        "toString()",
                        "toString",
                        String.class.getName(),
                        String.class.getSimpleName());
    }

    private PreviousComponentOutput createOutput(Class<?>... payloadType) {
        List<String> attributes = emptyList();
        List<String> payload = Arrays.stream(payloadType).map(Class::getName).collect(toList());
        return new PreviousComponentOutputDefault(attributes, payload, EMPTY);
    }
}
