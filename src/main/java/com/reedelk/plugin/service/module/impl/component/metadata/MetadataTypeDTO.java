package com.reedelk.plugin.service.module.impl.component.metadata;

import java.util.Collection;

public class MetadataTypeDTO {

    private final String type;
    private final TypeProxy typeProxy;
    private final Collection<MetadataTypeItemDTO> properties;

    public MetadataTypeDTO(Class<?> clazz, Collection<MetadataTypeItemDTO> properties) {
        this.type = clazz.getSimpleName();
        this.typeProxy = TypeProxy.create(clazz);
        this.properties = properties;
    }

    public MetadataTypeDTO(String displayType, TypeProxy typeProxy, Collection<MetadataTypeItemDTO> properties) {
        this.type = displayType;
        this.typeProxy = typeProxy;
        this.properties = properties;
    }

    public String getType() {
        return type;
    }

    public TypeProxy getTypeProxy() {
        return typeProxy;
    }

    public Collection<MetadataTypeItemDTO> getProperties() {
        return properties;
    }
}
