package com.reedelk.plugin.service.module.impl.component.metadata;

public class MetadataDTO {

    private final MetadataExpectedInputDTO expectedInput;
    private final MetadataActualInputDTO actualInput;

    public MetadataDTO(MetadataActualInputDTO actualInput, MetadataExpectedInputDTO expectedInput) {
        this.expectedInput = expectedInput;
        this.actualInput = actualInput;
    }

    public MetadataExpectedInputDTO getExpectedInput() {
        return expectedInput;
    }

    public MetadataActualInputDTO getActualInput() {
        return actualInput;
    }
}
