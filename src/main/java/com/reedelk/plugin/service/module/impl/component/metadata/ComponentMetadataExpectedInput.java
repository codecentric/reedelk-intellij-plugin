package com.reedelk.plugin.service.module.impl.component.metadata;

import java.util.List;

public class ComponentMetadataExpectedInput {

    private final String description;
    private final List<String> payload;

    public ComponentMetadataExpectedInput(List<String> payload, String description) {
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
