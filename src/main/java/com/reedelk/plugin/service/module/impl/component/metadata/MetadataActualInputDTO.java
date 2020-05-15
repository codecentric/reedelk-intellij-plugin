package com.reedelk.plugin.service.module.impl.component.metadata;

import java.util.List;

public class MetadataActualInputDTO {

    // Is multiple messages is true when the component is the first
    // after a fork or for each scope and the component itself has type JOIN,
    // where multiple messages are
    // being returned to be joined.
    private final boolean isMultipleMessages;
    private final String payloadDescription;
    private final MetadataTypeDTO attributes;
    private final List<MetadataTypeDTO> payload;

    public MetadataActualInputDTO() {
        this.isMultipleMessages = true;
        this.payloadDescription = null;
        this.attributes = null;
        this.payload = null;
    }

    public MetadataActualInputDTO(MetadataTypeDTO attributes,
                                  List<MetadataTypeDTO> payload,
                                  String payloadDescription) {
        this.isMultipleMessages = false;
        this.payloadDescription = payloadDescription;
        this.attributes = attributes;
        this.payload = payload;
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

    public boolean isMultipleMessages() {
        return isMultipleMessages;
    }
}
