package com.reedelk.plugin.service.module.impl.component.metadata;

import java.util.Collection;

public class MetadataTypeDTO {

    private final String type;
    private final Collection<MetadataTypeItemDTO> properties;

    public MetadataTypeDTO(String type, Collection<MetadataTypeItemDTO> properties) {
        this.type = type;
        this.properties = properties;
    }

    public String getType() {
        return type;
    }

    public Collection<MetadataTypeItemDTO> getProperties() {
        return properties;
    }
}
