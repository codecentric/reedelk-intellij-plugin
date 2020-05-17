package com.reedelk.plugin.service.module.impl.component.metadata;

import java.util.Collection;

public class MetadataTypeDTO {

    private final String type;
    private final String fullyQualifiedType;
    private final Collection<MetadataTypeItemDTO> properties;

    public MetadataTypeDTO(String type, String fullyQualifiedType, Collection<MetadataTypeItemDTO> properties) {
        this.type = type;
        this.properties = properties;
        this.fullyQualifiedType = fullyQualifiedType;
    }

    public String getType() {
        return type;
    }

    public String getFullyQualifiedType() {
        return fullyQualifiedType;
    }

    public Collection<MetadataTypeItemDTO> getProperties() {
        return properties;
    }
}
