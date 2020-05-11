package com.reedelk.plugin.service.module.impl.component.metadata;

import java.util.Optional;

public class ComponentMetadata {

    private final ComponentMetadataExpectedInput expectedInput;
    private final ComponentMetadataActualInput actualInput;

    public ComponentMetadata(ComponentMetadataActualInput actualInput, ComponentMetadataExpectedInput expectedInput) {
        this.expectedInput = expectedInput;
        this.actualInput = actualInput;
    }

    public Optional<ComponentMetadataExpectedInput> getExpectedInput() {
        return Optional.ofNullable(expectedInput);
    }

    public Optional<ComponentMetadataActualInput> getActualInput() {
        return Optional.ofNullable(actualInput);
    }
}
