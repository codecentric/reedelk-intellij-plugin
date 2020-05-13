package com.reedelk.plugin.service.module.impl.component.metadata;

import java.util.List;

public class ComponentMetadataExpectedInputDTO {

    private final String description;
    private final List<String> payload;

    public ComponentMetadataExpectedInputDTO(List<String> payload, String description) {
        this.payload = payload;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getPayload() {
        return payload;
    }
}
