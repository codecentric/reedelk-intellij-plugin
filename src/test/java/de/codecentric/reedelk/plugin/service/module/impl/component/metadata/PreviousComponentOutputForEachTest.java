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
class PreviousComponentOutputForEachTest extends AbstractComponentDiscoveryTest {
    
    private final String TEST_DESCRIPTION = "My description";

    private SuggestionFinder suggestionFinder;

    @BeforeEach
    public void setUp() {
        super.setUp();
        suggestionFinder = new SuggestionFinderDefault(typeAndTries);
    }

    @Test
    void shouldExtractListItemTypeFromPreviousOutput() {
        // Given
        PreviousComponentOutputDefault previousOutput = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(TypeTestUtils.ListMyTypeWithMethodsAndProperties.class.getName()),
                TEST_DESCRIPTION);
        
        PreviousComponentOutputForEach output = new PreviousComponentOutputForEach(previousOutput);
        
        // When
        List<MetadataTypeDTO> actualMetadata = output.mapPayload(suggestionFinder, typeAndTries);

        // Then
        MetadataTypeDTO actual = actualMetadata.iterator().next();

        PluginAssertion.assertThat(actual)
                .hasDisplayType(TypeTestUtils.MyTypeWithMethodsAndProperties.class.getSimpleName())
                .hasProperty("property1").withDisplayType("String").and()
                .hasProperty("property2").withDisplayType("long").and()
                .hasProperty("property3").withDisplayType("Double");
    }

    @Test
    void shouldReturnPrimitiveTypeWhenPreviousOutputIsPrimitive() {
        // Given
        PreviousComponentOutputDefault previousOutput = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(String.class.getName()),
                TEST_DESCRIPTION);

        PreviousComponentOutputForEach output = new PreviousComponentOutputForEach(previousOutput);

        // When
        List<MetadataTypeDTO> actualMetadata = output.mapPayload(suggestionFinder, typeAndTries);

        // Then
        MetadataTypeDTO actual = actualMetadata.iterator().next();

        PluginAssertion.assertThat(actual)
                .hasType(String.class.getName())
                .hasDisplayType(String.class.getSimpleName())
                .hasNoProperties();
    }
}
