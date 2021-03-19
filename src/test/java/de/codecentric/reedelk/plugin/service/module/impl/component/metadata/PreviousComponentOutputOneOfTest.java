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

import java.util.HashSet;
import java.util.List;

import static de.codecentric.reedelk.plugin.service.module.impl.component.completion.TypeTestUtils.MyItemType;
import static de.codecentric.reedelk.plugin.service.module.impl.component.completion.TypeTestUtils.MyTypeWithMethodsAndProperties;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PreviousComponentOutputOneOfTest extends AbstractComponentDiscoveryTest {

    private final String TEST_DESCRIPTION = "My description";

    private SuggestionFinder suggestionFinder;

    @BeforeEach
    public void setUp() {
        super.setUp();
        suggestionFinder = new SuggestionFinderDefault(typeAndTries);
    }

    @Test
    void shouldReturnCorrectPreviousOutputFromComplexType() {
        // Given
        PreviousComponentOutputDefault previousOutput = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(MyTypeWithMethodsAndProperties.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputOneOf output = new PreviousComponentOutputOneOf(new HashSet<>(singletonList(previousOutput)));

        // When
        List<MetadataTypeDTO> actualMetadata = output.mapPayload(suggestionFinder, typeAndTries);

        // Then
        PluginAssertion.assertThat(actualMetadata)
                .contains(MyTypeWithMethodsAndProperties.class.getName(), "MyTypeWithMethodsAndProperties")
                .hasProperty("property1").withDisplayType("String").and()
                .hasProperty("property2").withDisplayType("long").and()
                .hasProperty("property3").withDisplayType("Double");
    }

    @Test
    void shouldReturnCorrectPreviousOutputFromDifferentComplexTypes() {
        // Given
        PreviousComponentOutputDefault previousOutput1 = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(MyTypeWithMethodsAndProperties.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputDefault previousOutput2 = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(MyItemType.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputOneOf output =
                new PreviousComponentOutputOneOf(new HashSet<>(asList(previousOutput1, previousOutput2)));

        // When
        List<MetadataTypeDTO> actualMetadata = output.mapPayload(suggestionFinder, typeAndTries);

        // Then
        PluginAssertion.assertThat(actualMetadata)
                .contains(MyTypeWithMethodsAndProperties.class.getName(), "MyTypeWithMethodsAndProperties")
                .hasProperty("property1").withDisplayType("String").and()
                .hasProperty("property2").withDisplayType("long").and()
                .hasProperty("property3").withDisplayType("Double").and()
                .and()
                .contains(MyItemType.class.getName(), "TypeTestUtils$MyItemType")
                .hasNoProperties();
    }

    @Test
    void shouldReturnCorrectPreviousOutputFromSimpleType() {
        // Given
        PreviousComponentOutputDefault previousOutput = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(String.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputOneOf output = new PreviousComponentOutputOneOf(new HashSet<>(singletonList(previousOutput)));

        // When
        List<MetadataTypeDTO> actualMetadata = output.mapPayload(suggestionFinder, typeAndTries);

        // Then
        PluginAssertion.assertThat(actualMetadata)
                .contains(String.class.getName(), "String").hasNoProperties();
    }

    @Test
    void shouldReturnCorrectPreviousOutputFromDifferentSimpleTypes() {
        // Given
        PreviousComponentOutputDefault previousOutput1 = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(String.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputDefault previousOutput2 = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(Long.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputOneOf output =
                new PreviousComponentOutputOneOf(new HashSet<>(asList(previousOutput1, previousOutput2)));

        // When
        List<MetadataTypeDTO> actualMetadataList = output.mapPayload(suggestionFinder, typeAndTries);

        // Then
        PluginAssertion.assertThat(actualMetadataList)
                .contains(String.class.getName(), "String").hasNoProperties().and()
                .contains(Long.class.getName(), "Long").hasNoProperties();
    }

    @Test
    void shouldReturnCorrectPreviousOutputWhenOutputsAreEmpty() {
        // Given
        PreviousComponentOutputOneOf output = new PreviousComponentOutputOneOf(new HashSet<>());

        // When
        List<MetadataTypeDTO> actualMetadataList = output.mapPayload(suggestionFinder, typeAndTries);

        // Then
        assertThat(actualMetadataList.isEmpty());
    }

    // We expect merged attributes
    @Test
    void shouldCorrectlyMapAttributes() {
        // Given
        PreviousComponentOutputDefault previousOutput1 = new PreviousComponentOutputDefault(
                singletonList(TypeTestUtils.MyAttributeType.class.getName()),
                singletonList(String.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputDefault previousOutput2 = new PreviousComponentOutputDefault(
                singletonList(TypeTestUtils.MySecondAttributeType.class.getName()),
                singletonList(String.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputOneOf output =
                new PreviousComponentOutputOneOf(new HashSet<>(asList(previousOutput1, previousOutput2)));

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
}
