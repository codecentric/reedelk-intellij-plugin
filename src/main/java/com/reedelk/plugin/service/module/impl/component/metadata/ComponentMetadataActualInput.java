package com.reedelk.plugin.service.module.impl.component.metadata;

import java.util.List;

public class ComponentMetadataActualInput {

    private final String payloadDescription;
    private final MetadataTypeDescriptor attributes;
    private final List<MetadataTypeDescriptor> payload;

    public ComponentMetadataActualInput(MetadataTypeDescriptor attributes,
                                        List<MetadataTypeDescriptor> payload,
                                        String payloadDescription) {
        this.attributes = attributes;
        this.payload = payload;
        this.payloadDescription = payloadDescription;
    }

    public MetadataTypeDescriptor getAttributes() {
        return attributes;
    }

    public String getPayloadDescription() {
        return payloadDescription;
    }

    public List<MetadataTypeDescriptor> getPayload() {
        return payload;
    }
}
