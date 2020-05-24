package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.service.module.impl.component.completion.SuggestionFinder;
import com.reedelk.plugin.service.module.impl.component.completion.SuggestionFinderDefault;
import com.reedelk.plugin.service.module.impl.component.completion.TypeTestUtils;
import com.reedelk.runtime.api.message.MessageAttributes;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.reedelk.plugin.service.module.impl.component.completion.TypeTestUtils.CustomMessageAttributeType1;
import static com.reedelk.plugin.service.module.impl.component.completion.TypeTestUtils.CustomMessageAttributeType2;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

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
        Assertions.assertThat(actual).isEqualTo(TEST_DESCRIPTION);
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
                singletonList(TypeTestUtils.MyTypeWithMethodsAndProperties.class.getName()),
                TEST_DESCRIPTION);

        // When
        List<MetadataTypeDTO> actual = outputDefault.mapPayload(suggestionFinder, typeAndTries);

        // Then
        MetadataTypeDTO dto = actual.iterator().next();

        PluginAssertion.assertThat(dto)
                .hasDisplayType(TypeTestUtils.MyTypeWithMethodsAndProperties.class.getSimpleName())
                .hasType(TypeTestUtils.MyTypeWithMethodsAndProperties.class.getName())
                .hasPropertyCount(3)
                .hasProperty("property1").withDisplayType("String").and()
                .hasProperty("property2").withDisplayType("long").and()
                .hasProperty("property3").withDisplayType("Double");
    }
}
