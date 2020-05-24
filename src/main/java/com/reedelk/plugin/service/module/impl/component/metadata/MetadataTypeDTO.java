package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.service.module.impl.component.completion.TypeProxy;

import java.util.Collection;

public class MetadataTypeDTO {

    private final String displayType;
    private final TypeProxy typeProxy;
    private final Collection<MetadataTypeItemDTO> properties;

    public MetadataTypeDTO(Class<?> clazz, Collection<MetadataTypeItemDTO> properties) {
        this.displayType = clazz.getSimpleName();
        this.typeProxy = TypeProxy.create(clazz);
        this.properties = properties;
    }

    public MetadataTypeDTO(String displayType, TypeProxy typeProxy, Collection<MetadataTypeItemDTO> properties) {
        this.displayType = displayType;
        this.typeProxy = typeProxy;
        this.properties = properties;
    }

    public String getDisplayType() {
        return displayType;
    }

    public TypeProxy getTypeProxy() {
        return typeProxy;
    }

    public Collection<MetadataTypeItemDTO> getProperties() {
        return properties;
    }
}
