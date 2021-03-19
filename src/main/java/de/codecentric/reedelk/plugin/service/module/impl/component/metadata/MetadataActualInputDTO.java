package de.codecentric.reedelk.plugin.service.module.impl.component.metadata;

import java.util.List;

public class MetadataActualInputDTO {

    // Is multiple messages is true when the component is the first
    // after a fork or for each scope and the component itself has type JOIN,
    // where multiple messages are
    // being returned to be joined.
    private final boolean isMultipleMessages;
    private final boolean isNotFound;
    private final String payloadDescription;
    private final MetadataTypeDTO attributes;
    private final List<MetadataTypeDTO> payload;

    public MetadataActualInputDTO(MetadataTypeDTO attributes,
                                  List<MetadataTypeDTO> payload,
                                  String payloadDescription) {
        this(attributes, payload, payloadDescription, false, false);
    }

    public MetadataActualInputDTO(boolean isNotFound) {
        this(null, null, null, false, isNotFound);
    }

    public MetadataActualInputDTO(MetadataTypeDTO attributes,
                                  List<MetadataTypeDTO> payload,
                                  String payloadDescription,
                                  boolean isMultipleMessages,
                                  boolean isNotFound) {
        this.isNotFound = isNotFound;
        this.isMultipleMessages = isMultipleMessages;
        this.payload = payload;
        this.attributes = attributes;
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

    public boolean isMultipleMessages() {
        return isMultipleMessages;
    }

    public boolean isNotFound() {
        return isNotFound;
    }
}
