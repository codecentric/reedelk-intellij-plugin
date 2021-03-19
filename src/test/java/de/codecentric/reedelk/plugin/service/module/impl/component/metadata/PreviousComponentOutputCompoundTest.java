package de.codecentric.reedelk.plugin.service.module.impl.component.metadata;

import de.codecentric.reedelk.plugin.service.module.impl.component.completion.SuggestionFinder;
import de.codecentric.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import de.codecentric.reedelk.plugin.service.module.impl.component.metadata.MetadataTypeDTO;
import de.codecentric.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutput;
import de.codecentric.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutputCompound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PreviousComponentOutputCompoundTest {

    private PreviousComponentOutputCompound output;
    @Mock
    private SuggestionFinder suggestionFinder;
    @Mock
    private TypeAndTries typeAndTries;

    @BeforeEach
    public void setUp() {

    }

    @Test
    void shouldDelegatePayloadMappingCorrectly() {
        // Given
        PreviousComponentOutput payloadOutput = mock(PreviousComponentOutput.class);
        PreviousComponentOutput attributesOutput = mock(PreviousComponentOutput.class);
        PreviousComponentOutputCompound output =
                new PreviousComponentOutputCompound(attributesOutput, payloadOutput);

        List<MetadataTypeDTO> expectedMappedPayload = new ArrayList<>();
        doReturn(expectedMappedPayload)
                .when(payloadOutput).mapPayload(suggestionFinder, typeAndTries);

        // When
        List<MetadataTypeDTO> actualMappedPayload = output.mapPayload(suggestionFinder, typeAndTries);

        // Then
        assertThat(actualMappedPayload).isEqualTo(expectedMappedPayload);
        verify(payloadOutput).mapPayload(suggestionFinder, typeAndTries);
        verifyNoMoreInteractions(payloadOutput, attributesOutput);
    }

    @Test
    void shouldDelegateAttributesMappingCorrectly() {
        // Given
        PreviousComponentOutput payloadOutput = mock(PreviousComponentOutput.class);
        PreviousComponentOutput attributesOutput = mock(PreviousComponentOutput.class);
        PreviousComponentOutputCompound output =
                new PreviousComponentOutputCompound(attributesOutput, payloadOutput);

        MetadataTypeDTO expectedMappedAttributes = Mockito.mock(MetadataTypeDTO.class);
        doReturn(expectedMappedAttributes)
                .when(attributesOutput).mapAttributes(suggestionFinder, typeAndTries);

        // When
        MetadataTypeDTO actualMappedAttributes = output.mapAttributes(suggestionFinder, typeAndTries);

        // Then
        assertThat(actualMappedAttributes).isEqualTo(expectedMappedAttributes);
        verify(attributesOutput).mapAttributes(suggestionFinder, typeAndTries);
        verifyNoMoreInteractions(payloadOutput, attributesOutput);
    }

}
