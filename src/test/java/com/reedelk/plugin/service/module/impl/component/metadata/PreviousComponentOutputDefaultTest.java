package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.service.module.impl.component.completion.SuggestionFinder;
import com.reedelk.plugin.service.module.impl.component.completion.SuggestionFinderDefault;
import com.reedelk.runtime.api.message.MessageAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PreviousComponentOutputDefaultTest extends AbstractComponentDiscoveryTest {

    private SuggestionFinder suggestionFinder;

    @BeforeEach
    public void setUp() {
        super.setUp();
        suggestionFinder = new SuggestionFinderDefault(typeAndTries);
    }

    @Test
    void shouldDoSomething() {
        // Given
        PreviousComponentOutputDefault outputDefault = new PreviousComponentOutputDefault(
                        singletonList(MessageAttributes.class.getName()),
                        singletonList(String.class.getName()),
                        "My description");

        // When
        MetadataTypeDTO metadataTypeDTO = outputDefault.mapAttributes(suggestionFinder, typeAndTries);

        // Then
        assertThat(metadataTypeDTO).isNotNull();
    }
}
