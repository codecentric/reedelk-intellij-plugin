package de.codecentric.reedelk.plugin.service.module.impl.component.metadata;

import de.codecentric.reedelk.plugin.assertion.PluginAssertion;
import de.codecentric.reedelk.plugin.service.module.impl.component.completion.SuggestionFinder;
import de.codecentric.reedelk.plugin.service.module.impl.component.completion.SuggestionFinderDefault;
import de.codecentric.reedelk.plugin.service.module.impl.component.completion.TypeTestUtils;
import de.codecentric.reedelk.runtime.api.message.MessageAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.Collections.singletonList;

@ExtendWith(MockitoExtension.class)
class PreviousComponentOutputInferFromDynamicExpressionTest extends AbstractComponentDiscoveryTest {

    private final String TEST_DESCRIPTION = "My description";

    private SuggestionFinder suggestionFinder;

    @BeforeEach
    public void setUp() {
        super.setUp();
        suggestionFinder = new SuggestionFinderDefault(typeAndTries);
    }

    @Test
    void shouldReturnCorrectMetadataTypeWhenEvaluateMessagePayload() {
        // Given
        String dynamicExpression = "#[message.payload()]";
        PreviousComponentOutputDefault previousOutput = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(TypeTestUtils.MyTypeWithMethodsAndProperties.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputInferFromDynamicExpression output =
                new PreviousComponentOutputInferFromDynamicExpression(previousOutput, dynamicExpression);

        // When
        List<MetadataTypeDTO> actual = output.mapPayload(suggestionFinder, typeAndTries);

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

    @Test
    void shouldReturnCorrectMetadataTypeWhenEvaluateMessagePayloadProperty() {
        // Given
        String dynamicExpression = "#[message.payload().property3]";
        PreviousComponentOutputDefault previousOutput = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(TypeTestUtils.MyTypeWithMethodsAndProperties.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputInferFromDynamicExpression output =
                new PreviousComponentOutputInferFromDynamicExpression(previousOutput, dynamicExpression);

        // When
        List<MetadataTypeDTO> actual = output.mapPayload(suggestionFinder, typeAndTries);

        // Then
        MetadataTypeDTO dto = actual.iterator().next();

        PluginAssertion.assertThat(dto)
                .hasDisplayType("Double")
                .hasType(Double.class.getName())
                .hasPropertyCount(0);
    }

    @Test
    void shouldReturnGenericObjectIfDynamicExpressionIsLiteral() {
        // Given
        String dynamicExpression = "#['my text']";
        PreviousComponentOutputDefault previousOutput = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(TypeTestUtils.MyTypeWithMethodsAndProperties.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputInferFromDynamicExpression output =
                new PreviousComponentOutputInferFromDynamicExpression(previousOutput, dynamicExpression);

        // When
        List<MetadataTypeDTO> actual = output.mapPayload(suggestionFinder, typeAndTries);

        // Then
        MetadataTypeDTO dto = actual.iterator().next();

        PluginAssertion.assertThat(dto)
                .hasDisplayType("Object")
                .hasType(Object.class.getName())
                .hasPropertyCount(0);
    }

    @Test
    void shouldReturnAttributesFromPreviousOutput() {
        // Given
        String dynamicExpression = "#['my text']";
        PreviousComponentOutputDefault previousOutput = new PreviousComponentOutputDefault(
                singletonList(TypeTestUtils.MyAttributeType.class.getName()),
                singletonList(TypeTestUtils.MyTypeWithMethodsAndProperties.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputInferFromDynamicExpression output =
                new PreviousComponentOutputInferFromDynamicExpression(previousOutput, dynamicExpression);

        // When
        MetadataTypeDTO actual = output.mapAttributes(suggestionFinder, typeAndTries);

        // Then
        PluginAssertion.assertThat(actual)
                .hasDisplayType(MessageAttributes.class.getSimpleName())
                .hasType(TypeTestUtils.MyAttributeType.class.getName())
                .hasPropertyCount(3)
                .hasProperty("component").withDisplayType("String").and()
                .hasProperty("attributeProperty1").withDisplayType("String").and()
                .hasProperty("attributeProperty2").withDisplayType("long");
    }
}
