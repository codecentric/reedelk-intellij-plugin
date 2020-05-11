package com.reedelk.plugin.service.module.impl.component.metadata;

import java.util.List;

public class ComponentMetadata {

    private final String payloadDescription;
    private final MetadataTypeDescriptor attributes;
    private final List<MetadataTypeDescriptor> payload;

    public ComponentMetadata(MetadataTypeDescriptor attributes, List<MetadataTypeDescriptor> payload, String payloadDescription) {
        this.payload = payload;
        this.attributes = attributes;
        this.payloadDescription = payloadDescription;
    }

    public MetadataTypeDescriptor getAttributes() {
        return attributes;
    }

    public List<MetadataTypeDescriptor> getPayload() {
        return payload;
    }

    public String getPayloadDescription() {
        return payloadDescription;
    }
}
