package com.reedelk.plugin.service.module.impl.component.metadata;

import java.util.Collection;

public class MetadataTypeDescriptor {

    private final String type;
    private final Collection<MetadataTypeItemDescriptor> properties;

    public MetadataTypeDescriptor(String type, Collection<MetadataTypeItemDescriptor> properties) {
        this.type = type;
        this.properties = properties;
    }

    public String getType() {
        return type;
    }

    public Collection<MetadataTypeItemDescriptor> getProperties() {
        return properties;
    }
}
