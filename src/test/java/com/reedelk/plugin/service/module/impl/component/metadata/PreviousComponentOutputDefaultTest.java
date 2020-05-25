package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.service.module.impl.component.completion.SuggestionFinder;
import com.reedelk.plugin.service.module.impl.component.completion.SuggestionFinderDefault;
import com.reedelk.runtime.api.message.MessageAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.reedelk.plugin.service.module.impl.component.completion.TypeTestUtils.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PreviousComponentOutputDefaultTest extends AbstractComponentDiscoveryTest {

    private final String TEST_DESCRIPTION = "My description";

    private SuggestionFinder suggestionFinder;

    @BeforeEach
    public void setUp() {
        super.setUp();
        suggestionFinder = new SuggestionFinderDefault(typeAndTries);
    }

    @Test
    void shouldReturnCorrectDescription() {
        // Given
        PreviousComponentOutputDefault outputDefault = new PreviousComponentOutputDefault(
                singletonList(CustomMessageAttributeType1.class.getName()),
                singletonList(String.class.getName()),
                TEST_DESCRIPTION);

        // When
        String actual = outputDefault.description();

        // Then
        assertThat(actual).isEqualTo(TEST_DESCRIPTION);
    }

    @Test
    void shouldCorrectlyMapAttributesMetadata() {
        // Given
        PreviousComponentOutputDefault outputDefault = new PreviousComponentOutputDefault(
                        singletonList(CustomMessageAttributeType1.class.getName()),
                        singletonList(String.class.getName()),
                TEST_DESCRIPTION);

        // When
        MetadataTypeDTO actual = outputDefault.mapAttributes(suggestionFinder, typeAndTries);

        // Then
        PluginAssertion.assertThat(actual)
                .hasDisplayType(MessageAttributes.class.getSimpleName())
                .hasProperty("component").withDisplayType("String").and()
                .hasProperty("attributeProperty1").withDisplayType("String").and()
                .hasProperty("attributeProperty2").withDisplayType("long");
    }

    @Test
    void shouldCorrectlyMapAndMergeMultipleAttributesMetadata() {
        // Given
        PreviousComponentOutputDefault outputDefault = new PreviousComponentOutputDefault(
                asList(CustomMessageAttributeType1.class.getName(), CustomMessageAttributeType2.class.getName()),
                singletonList(String.class.getName()),
                TEST_DESCRIPTION);

        // When
        MetadataTypeDTO actual = outputDefault.mapAttributes(suggestionFinder, typeAndTries);

        // Then
        PluginAssertion.assertThat(actual)
                .hasDisplayType(MessageAttributes.class.getSimpleName())
                .hasType(MessageAttributes.class.getName())
                .hasPropertyCount(4)
                .hasProperty("component").withDisplayType("String").and()
                .hasProperty("attributeProperty1").withDisplayType("String,int").and()
                .hasProperty("attributeProperty2").withDisplayType("long").and()
                .hasProperty("anotherAttributeProperty3").withDisplayType("long");
    }

    @Test
    void shouldCorrectlyMapPayloadMetadata() {
        // Given
        PreviousComponentOutputDefault outputDefault = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(MyTypeWithMethodsAndProperties.class.getName()),
                TEST_DESCRIPTION);

        // When
        List<MetadataTypeDTO> actual = outputDefault.mapPayload(suggestionFinder, typeAndTries);

        // Then
        MetadataTypeDTO dto = actual.iterator().next();

        PluginAssertion.assertThat(dto)
                .hasDisplayType(MyTypeWithMethodsAndProperties.class.getSimpleName())
                .hasType(MyTypeWithMethodsAndProperties.class.getName())
                .hasPropertyCount(3)
                .hasProperty("property1").withDisplayType("String").and()
                .hasProperty("property2").withDisplayType("long").and()
                .hasProperty("property3").withDisplayType("Double");
    }

    @Test
    void shouldCorrectlyMapPayloadMetadataSimpleList() {
        // Given
        PreviousComponentOutputDefault outputDefault = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(ListSimpleType.class.getName()),
                TEST_DESCRIPTION);

        // When
        List<MetadataTypeDTO> actual = outputDefault.mapPayload(suggestionFinder, typeAndTries);

        // Then
        MetadataTypeDTO dto = actual.iterator().next();

        PluginAssertion.assertThat(dto)
                .hasDisplayType("List<String>")
                .hasType(ListSimpleType.class.getName());
    }

    @Test
    void shouldCorrectlyMapPayloadMetadataComplexListAndUnrollComplexObjectProperties() {
        // Given
        PreviousComponentOutputDefault outputDefault = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(ListMyTypeWithMethodsAndProperties.class.getName()),
                TEST_DESCRIPTION);

        // When
        List<MetadataTypeDTO> actual = outputDefault.mapPayload(suggestionFinder, typeAndTries);

        // Then
        MetadataTypeDTO dto = actual.iterator().next();

        PluginAssertion.assertThat(dto)
                .hasDisplayType("List<MyTypeWithMethodsAndProperties> : MyTypeWithMethodsAndProperties")
                .hasType(ListMyTypeWithMethodsAndProperties.class.getName())
                .hasPropertyCount(3)
                .hasProperty("property1").withDisplayType("String").and()
                .hasProperty("property2").withDisplayType("long").and()
                .hasProperty("property3").withDisplayType("Double");
    }
}
