package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.service.module.impl.component.completion.SuggestionFinder;
import com.reedelk.plugin.service.module.impl.component.completion.SuggestionFinderDefault;
import com.reedelk.runtime.api.message.MessageAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.reedelk.plugin.service.module.impl.component.completion.TypeTestUtils.CustomMessageAttributeType1;
import static com.reedelk.plugin.service.module.impl.component.completion.TypeTestUtils.CustomMessageAttributeType2;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

@ExtendWith(MockitoExtension.class)
class PreviousComponentOutputDefaultTest extends AbstractComponentDiscoveryTest {

    private SuggestionFinder suggestionFinder;

    @BeforeEach
    public void setUp() {
        super.setUp();
        suggestionFinder = new SuggestionFinderDefault(typeAndTries);
    }

    @Test
    void shouldCorrectlyMapAttributesMetadata() {
        // Given
        PreviousComponentOutputDefault outputDefault = new PreviousComponentOutputDefault(
                        singletonList(CustomMessageAttributeType1.class.getName()),
                        singletonList(String.class.getName()),
                        "My description");

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
                "My description");

        // When
        MetadataTypeDTO actual = outputDefault.mapAttributes(suggestionFinder, typeAndTries);

        // Then
        PluginAssertion.assertThat(actual)
                .hasDisplayType(MessageAttributes.class.getSimpleName())
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
                asList(CustomMessageAttributeType1.class.getName(), CustomMessageAttributeType2.class.getName()),
                singletonList(String.class.getName()),
                "My description");

        // When
        MetadataTypeDTO actual = outputDefault.mapAttributes(suggestionFinder, typeAndTries);

        // Then
        PluginAssertion.assertThat(actual)
                .hasDisplayType(MessageAttributes.class.getSimpleName())
                .hasPropertyCount(4)
                .hasProperty("component").withDisplayType("String").and()
                .hasProperty("attributeProperty1").withDisplayType("String,int").and()
                .hasProperty("attributeProperty2").withDisplayType("long").and()
                .hasProperty("anotherAttributeProperty3").withDisplayType("long");
    }
}
