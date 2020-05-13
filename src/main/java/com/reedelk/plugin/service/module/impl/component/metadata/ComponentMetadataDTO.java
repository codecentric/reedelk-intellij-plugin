package com.reedelk.plugin.service.module.impl.component.metadata;

import java.util.Optional;

public class ComponentMetadataDTO {

    private final ComponentMetadataExpectedInputDTO expectedInput;
    private final ComponentMetadataActualInputDTO actualInput;

    public ComponentMetadataDTO(ComponentMetadataActualInputDTO actualInput, ComponentMetadataExpectedInputDTO expectedInput) {
        this.expectedInput = expectedInput;
        this.actualInput = actualInput;
    }

    public Optional<ComponentMetadataExpectedInputDTO> getExpectedInput() {
        return Optional.ofNullable(expectedInput);
    }

    public Optional<ComponentMetadataActualInputDTO> getActualInput() {
        return Optional.ofNullable(actualInput);
    }
}
