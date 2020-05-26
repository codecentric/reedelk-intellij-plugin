package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.service.module.impl.component.completion.*;
import com.reedelk.runtime.api.message.MessageAttributes;
import com.reedelk.runtime.api.message.MessagePayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.FUNCTION;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@ExtendWith(MockitoExtension.class)
class PreviousComponentOutputJoinTest extends AbstractComponentDiscoveryTest {

    private final String TEST_DESCRIPTION = "My description";

    private SuggestionFinder suggestionFinder;

    @BeforeEach
    public void setUp() {
        super.setUp();
        suggestionFinder = new SuggestionFinderDefault(typeAndTries);
    }

    @Test
    void shouldReturnListOfMyTypeWithMethodsAndPropertiesAndUnrollListTypeProperties() {
        // Given
        PreviousComponentOutputDefault previousOutput = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(TypeTestUtils.MyTypeWithMethodsAndProperties.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputJoin output = new PreviousComponentOutputJoin(new HashSet<>(singletonList(previousOutput)));

        // When
        List<MetadataTypeDTO> actualMetadata = output.mapPayload(suggestionFinder, typeAndTries);

        // Then
        MetadataTypeDTO actual = actualMetadata.iterator().next();

        PluginAssertion.assertThat(actual)
                .hasDisplayType("List<MyTypeWithMethodsAndProperties> : MyTypeWithMethodsAndProperties")
                .hasType(List.class.getName())
                .hasProperty("property1").withDisplayType("String").and()
                .hasProperty("property2").withDisplayType("long").and()
                .hasProperty("property3").withDisplayType("Double");
    }

    @Test
    void shouldReturnListOfObjectWhenJoiningBranchesHaveDifferentTypes() {
        // Given
        PreviousComponentOutputDefault previousOutput1 = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(TypeTestUtils.MyTypeWithMethodsAndProperties.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputDefault previousOutput2 = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(TypeTestUtils.MyItemType.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputJoin output =
                new PreviousComponentOutputJoin(new HashSet<>(asList(previousOutput1, previousOutput2)));

        // When
        List<MetadataTypeDTO> actualMetadata = output.mapPayload(suggestionFinder, typeAndTries);

        // Then
        MetadataTypeDTO actual = actualMetadata.iterator().next();

        PluginAssertion.assertThat(actual)
                .hasDisplayTypeContainingOneOf(
                        "List<TypeTestUtils$MyItemType,MyTypeWithMethodsAndProperties>",
                        "List<MyTypeWithMethodsAndProperties,TypeTestUtils$MyItemType>")
                .hasType(List.class.getName())
                .hasNoProperties();
    }

    @Test
    void shouldReturnListOfListWhenJoiningBranchOutputIsList() {
        // Given
        PreviousComponentOutputDefault previousOutput = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(TypeTestUtils.ListMyItemType.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputJoin output =
                new PreviousComponentOutputJoin(new HashSet<>(singletonList(previousOutput)));

        // When
        List<MetadataTypeDTO> actualMetadata = output.mapPayload(suggestionFinder, typeAndTries);

        // Then
        MetadataTypeDTO actual = actualMetadata.iterator().next();

        PluginAssertion.assertThat(actual)
                .hasDisplayType("List<List<TypeTestUtils$MyItemType>>")
                .hasType(List.class.getName())
                .hasNoProperties();
    }

    @Test
    void shouldReturnListOfListWhenJoiningBranchesOutputAreListsOfSameType() {
        // Given
        PreviousComponentOutputDefault previousOutput1 = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(TypeTestUtils.ListMyItemType.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputDefault previousOutput2 = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(TypeTestUtils.ListMyItemType.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputJoin output =
                new PreviousComponentOutputJoin(new HashSet<>(asList(previousOutput1, previousOutput2)));

        // When
        List<MetadataTypeDTO> actualMetadata = output.mapPayload(suggestionFinder, typeAndTries);

        // Then
        MetadataTypeDTO actual = actualMetadata.iterator().next();

        PluginAssertion.assertThat(actual)
                .hasDisplayType("List<List<TypeTestUtils$MyItemType>>")
                .hasType(List.class.getName())
                .hasNoProperties();
    }

    @Test
    void shouldReturnListOfListWhenJoiningBranchesOutputAreListsOfDifferentType() {
        // Given
        PreviousComponentOutputDefault previousOutput1 = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(TypeTestUtils.ListMyItemType.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputDefault previousOutput2 = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(TypeTestUtils.ListMyTypeWithMethodsAndProperties.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputJoin output =
                new PreviousComponentOutputJoin(new HashSet<>(asList(previousOutput1, previousOutput2)));

        // When
        List<MetadataTypeDTO> actualMetadata = output.mapPayload(suggestionFinder, typeAndTries);

        // Then
        MetadataTypeDTO actual = actualMetadata.iterator().next();

        PluginAssertion.assertThat(actual)
                .hasDisplayTypeContainingOneOf(
                        "List<List<MyTypeWithMethodsAndProperties>,List<TypeTestUtils$MyItemType>>",
                        "List<List<TypeTestUtils$MyItemType>,List<MyTypeWithMethodsAndProperties>>")
                .hasType(List.class.getName())
                .hasNoProperties();
    }

    @Test
    void shouldReturnListOfSimpleTypeWhenBranchesReturnSameType() {
        // Given
        PreviousComponentOutputDefault previousOutput1 = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(String.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputDefault previousOutput2 = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(String.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputJoin output =
                new PreviousComponentOutputJoin(new HashSet<>(asList(previousOutput1, previousOutput2)));

        // When
        List<MetadataTypeDTO> actualMetadata = output.mapPayload(suggestionFinder, typeAndTries);

        // Then
        MetadataTypeDTO actual = actualMetadata.iterator().next();

        PluginAssertion.assertThat(actual)
                .hasDisplayType("List<String>")
                .hasType(List.class.getName())
                .hasNoProperties();
    }

    @Test
    void shouldReturnListOfDifferentSimpleTypesWhenBranchesReturnDifferentTypes() {
        // Given
        PreviousComponentOutputDefault previousOutput1 = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(String.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputDefault previousOutput2 = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(Long.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputJoin output =
                new PreviousComponentOutputJoin(new HashSet<>(asList(previousOutput1, previousOutput2)));

        // When
        List<MetadataTypeDTO> actualMetadata = output.mapPayload(suggestionFinder, typeAndTries);

        // Then
        MetadataTypeDTO actual = actualMetadata.iterator().next();

        PluginAssertion.assertThat(actual)
                .hasDisplayTypeContainingOneOf("List<String,Long>","List<Long,String>")
                .hasType(List.class.getName())
                .hasNoProperties();
    }

    @Test
    void shouldReturnListOfObjectWhenNoOutputs() {
        // Given
        PreviousComponentOutputJoin output = new PreviousComponentOutputJoin(new HashSet<>());

        // When
        List<MetadataTypeDTO> actualMetadata = output.mapPayload(suggestionFinder, typeAndTries);

        // Then
        MetadataTypeDTO actual = actualMetadata.iterator().next();

        PluginAssertion.assertThat(actual)
                .hasDisplayTypeContainingOneOf("List<Object>")
                .hasType(List.class.getName())
                .hasNoProperties();
    }

    @Test
    void shouldBuildDynamicSuggestionsCorrectlyForBranchesReturningDifferentPrimitiveType() {
        // Given
        PreviousComponentOutputDefault previousOutput1 = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(String.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputDefault previousOutput2 = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(Long.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputJoin output =
                new PreviousComponentOutputJoin(new HashSet<>(asList(previousOutput1, previousOutput2)));

        Suggestion payload = Suggestion.create(FUNCTION)
                .insertValue("payload()")
                .returnType(TypeProxy.create(MessagePayload.class))
                .build();
        // When
        Collection<Suggestion> suggestions = output.buildDynamicSuggestions(suggestionFinder, payload, typeAndTries);

        // Then
        PluginAssertion.assertThat(suggestions)
                .contains(FUNCTION,
                        "payload()",
                        "payload()",
                        List.class.getName(),
                        "List<String,Long>","List<Long,String>")
                .hasSize(1);
    }

    @Test
    void shouldBuildDynamicSuggestionsCorrectlyForBranchesReturningSamePrimitiveType() {
        // Given
        PreviousComponentOutputDefault previousOutput1 = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(Long.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputDefault previousOutput2 = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(Long.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputJoin output =
                new PreviousComponentOutputJoin(new HashSet<>(asList(previousOutput1, previousOutput2)));

        Suggestion payload = Suggestion.create(FUNCTION)
                .insertValue("payload()")
                .returnType(TypeProxy.create(MessagePayload.class))
                .build();
        // When
        Collection<Suggestion> suggestions = output.buildDynamicSuggestions(suggestionFinder, payload, typeAndTries);

        // Then
        PluginAssertion.assertThat(suggestions)
                .contains(FUNCTION,
                        "payload()",
                        "payload()",
                        List.class.getName(),
                        "List<Long>")
                .hasSize(1);
    }

    // Returns List<Object>
    @Test
    void shouldBuildDynamicSuggestionsCorrectlyWhenNoBranches() {
        // Given
        PreviousComponentOutputJoin output =
                new PreviousComponentOutputJoin(new HashSet<>());

        Suggestion payload = Suggestion.create(FUNCTION)
                .insertValue("payload()")
                .returnType(TypeProxy.create(MessagePayload.class))
                .build();
        // When
        Collection<Suggestion> suggestions = output.buildDynamicSuggestions(suggestionFinder, payload, typeAndTries);

        // Then
        PluginAssertion.assertThat(suggestions)
                .contains(FUNCTION,
                        "payload()",
                        "payload()",
                        List.class.getName(),
                        "List<Object>")
                .hasSize(1);
    }
}
