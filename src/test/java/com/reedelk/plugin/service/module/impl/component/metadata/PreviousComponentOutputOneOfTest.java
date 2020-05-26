package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.service.module.impl.component.completion.SuggestionFinder;
import com.reedelk.plugin.service.module.impl.component.completion.SuggestionFinderDefault;
import com.reedelk.runtime.api.message.MessageAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;

import static com.reedelk.plugin.service.module.impl.component.completion.TypeTestUtils.MyTypeWithMethodsAndProperties;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

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
}
