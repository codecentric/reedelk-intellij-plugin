package com.reedelk.plugin.service.module.impl.component.metadata;

public class ComponentMetadataExpectedInputDTO {

    private final String description;
    private final String payload;

    public ComponentMetadataExpectedInputDTO(String payload, String description) {
        this.payload = payload;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getPayload() {
        return payload;
    }
}
