package de.codecentric.reedelk.plugin.service.module.impl.component.metadata;

import de.codecentric.reedelk.plugin.service.module.impl.component.completion.*;
import de.codecentric.reedelk.plugin.assertion.PluginAssertion;
import de.codecentric.reedelk.runtime.api.message.MessageAttributes;
import de.codecentric.reedelk.runtime.api.message.MessagePayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static de.codecentric.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.FUNCTION;
import static de.codecentric.reedelk.plugin.service.module.impl.component.completion.TypeTestUtils.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

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
                singletonList(MyTypeWithMethodsAndProperties.class.getName()),
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
                singletonList(MyTypeWithMethodsAndProperties.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputDefault previousOutput2 = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(MyItemType.class.getName()),
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
                singletonList(ListMyItemType.class.getName()),
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
                singletonList(ListMyItemType.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputDefault previousOutput2 = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(ListMyItemType.class.getName()),
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
                singletonList(ListMyItemType.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputDefault previousOutput2 = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(ListMyTypeWithMethodsAndProperties.class.getName()),
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

    @Test
    void shouldBuildDynamicSuggestionsCorrectlyForBranchesReturningSameComplexType() {
        // Given
        PreviousComponentOutputDefault previousOutput1 = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(MyTypeWithMethodsAndProperties.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputDefault previousOutput2 = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(MyTypeWithMethodsAndProperties.class.getName()),
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
                        "List<MyTypeWithMethodsAndProperties>")
                .hasSize(1);

        // Make sure that the list item type is correct.
        Suggestion next = suggestions.iterator().next();
        TypeProxy listItemType = next.getReturnType().resolve(typeAndTries).listItemType(typeAndTries);
        assertThat(listItemType.getTypeFullyQualifiedName()).isEqualTo(MyTypeWithMethodsAndProperties.class.getName());
    }

    @Test
    void shouldBuildDynamicSuggestionsCorrectlyForBranchesReturningSameListType() {
        // Given
        PreviousComponentOutputDefault previousOutput1 = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(ListMyTypeWithMethodsAndProperties.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputDefault previousOutput2 = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(ListMyTypeWithMethodsAndProperties.class.getName()),
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
                        "List<List<MyTypeWithMethodsAndProperties>>")
                .hasSize(1);

        // Make sure that the list item type is correct.
        Suggestion next = suggestions.iterator().next();
        TypeProxy listItemType = next.getReturnType().resolve(typeAndTries).listItemType(typeAndTries);
        assertThat(listItemType.getTypeFullyQualifiedName()).isEqualTo(ListMyTypeWithMethodsAndProperties.class.getName());

        Trie listMyTypeWithMethodsAndProperties = listItemType.resolve(typeAndTries);
        TypeProxy myMethodsAndPropertiesType = listMyTypeWithMethodsAndProperties.listItemType(typeAndTries);
        assertThat(myMethodsAndPropertiesType.getTypeFullyQualifiedName())
                .isEqualTo(MyTypeWithMethodsAndProperties.class.getName());
    }

    @Test
    void shouldBuildDynamicSuggestionsCorrectlyForBranchesReturningDifferentListType() {
        // Given
        PreviousComponentOutputDefault previousOutput1 = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(ListMyItemType.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputDefault previousOutput2 = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(ListMyTypeWithMethodsAndProperties.class.getName()),
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
        PluginAssertion.assertThat(suggestions).contains(FUNCTION,
                        "payload()",
                        "payload()",
                        List.class.getName(),
                        "List<List<TypeTestUtils$MyItemType>,List<MyTypeWithMethodsAndProperties>>",
                        "List<List<MyTypeWithMethodsAndProperties>,List<TypeTestUtils$MyItemType>>")
                .hasSize(1);

        // List item type should be object
        Trie resolve = suggestions.iterator().next().getReturnType().resolve(typeAndTries);
        TypeProxy listItemType = resolve.listItemType(typeAndTries);
        assertThat(listItemType.getTypeFullyQualifiedName()).isEqualTo(Object.class.getName());
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

    @Test
    void shouldNotJoinMessageAttributesWhenBranchesWithDifferentAttributeTypes() {
        // Given
        PreviousComponentOutputDefault previousOutput1 = new PreviousComponentOutputDefault(
                singletonList(MyAttributeType.class.getName()),
                singletonList(String.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputDefault previousOutput2 = new PreviousComponentOutputDefault(
                singletonList(MySecondAttributeType.class.getName()),
                singletonList(String.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputJoin output =
                new PreviousComponentOutputJoin(new HashSet<>(asList(previousOutput1, previousOutput2)));

        Suggestion payload = Suggestion.create(FUNCTION)
                .insertValue("attributes()")
                .returnType(TypeProxy.create(MessageAttributes.class))
                .build();
        // When
        Collection<Suggestion> suggestions = output.buildDynamicSuggestions(suggestionFinder, payload, typeAndTries);

        // Then
        PluginAssertion.assertThat(suggestions)
                .contains(FUNCTION,
                "attributes()",
                        "attributes()",
                        MyAttributeType.class.getName(),
                        "MessageAttributes")
                .contains(FUNCTION,
                        "attributes()",
                        "attributes()",
                        MySecondAttributeType.class.getName(),
                        "MessageAttributes")
                .hasSize(2);
    }

    // We expect merged attributes
    @Test
    void shouldCorrectlyMapAttributes() {
        // Given
        PreviousComponentOutputDefault previousOutput1 = new PreviousComponentOutputDefault(
                singletonList(MyAttributeType.class.getName()),
                singletonList(String.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputDefault previousOutput2 = new PreviousComponentOutputDefault(
                singletonList(MySecondAttributeType.class.getName()),
                singletonList(String.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputJoin output =
                new PreviousComponentOutputJoin(new HashSet<>(asList(previousOutput1, previousOutput2)));

        // When
        MetadataTypeDTO metadataTypeDTO = output.mapAttributes(suggestionFinder, typeAndTries);

        // Then
        PluginAssertion.assertThat(metadataTypeDTO)
                .hasPropertyCount(5)
                .hasProperty("component").withDisplayType("String").and()
                .hasProperty("secondAttributeProperty1").withDisplayType("String").and()
                .hasProperty("secondAttributeProperty2").withDisplayType("long").and()
                .hasProperty("attributeProperty1").withDisplayType("String").and()
                .hasProperty("attributeProperty2").withDisplayType("long");
    }

    @Test
    void shouldCorrectlyMapAttributesWhenEmptyOutputs() {
        // Given
        PreviousComponentOutputJoin output = new PreviousComponentOutputJoin(new HashSet<>());

        // When
        MetadataTypeDTO metadataTypeDTO = output.mapAttributes(suggestionFinder, typeAndTries);

        // Then
        PluginAssertion.assertThat(metadataTypeDTO)
                .hasDisplayType("MessageAttributes")
                .hasType(MessageAttributes.class.getName())
                .hasNoProperties();
    }
}
