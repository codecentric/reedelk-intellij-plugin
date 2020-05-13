package com.reedelk.plugin.service.module.impl.component.metadata;

import java.util.List;

public class ComponentMetadataActualInputDTO {

    private final String payloadDescription;
    private final MetadataTypeDTO attributes;
    private final List<MetadataTypeDTO> payload;

    public ComponentMetadataActualInputDTO(MetadataTypeDTO attributes,
                                           List<MetadataTypeDTO> payload,
                                           String payloadDescription) {
        this.attributes = attributes;
        this.payload = payload;
        this.payloadDescription = payloadDescription;
    }

    public MetadataTypeDTO getAttributes() {
        return attributes;
    }

    public String getPayloadDescription() {
        return payloadDescription;
    }

    public List<MetadataTypeDTO> getPayload() {
        return payload;
    }
}
