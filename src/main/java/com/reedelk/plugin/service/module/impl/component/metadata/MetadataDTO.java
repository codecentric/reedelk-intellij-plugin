package com.reedelk.plugin.service.module.impl.component.metadata;

import java.util.Optional;

public class MetadataDTO {

    private final MetadataExpectedInputDTO expectedInput;
    private final MetadataActualInputDTO actualInput;

    public MetadataDTO(MetadataActualInputDTO actualInput, MetadataExpectedInputDTO expectedInput) {
        this.expectedInput = expectedInput;
        this.actualInput = actualInput;
    }

    public Optional<MetadataExpectedInputDTO> getExpectedInput() {
        return Optional.ofNullable(expectedInput);
    }

    public Optional<MetadataActualInputDTO> getActualInput() {
        return Optional.ofNullable(actualInput);
    }
}
