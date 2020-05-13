package com.reedelk.plugin.service.module.impl.component.metadata;

public class MetadataTypeItemDTO {

    public final String name;
    public final String value;
    public final MetadataTypeDTO complex;

    public MetadataTypeItemDTO(String name, String value) {
        this.name = name;
        this.value = value;
        this.complex = null;
    }

    public MetadataTypeItemDTO(String name, MetadataTypeDTO complex) {
        this.name = name;
        this.value = null;
        this.complex = complex;
    }
}
