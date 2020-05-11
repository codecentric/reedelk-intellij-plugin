package com.reedelk.plugin.service.module.impl.component.metadata;

public class MetadataTypeItemDescriptor {

    public final String name;
    public final String value;
    public final MetadataTypeDescriptor complex;

    public MetadataTypeItemDescriptor(String name, String value) {
        this.name = name;
        this.value = value;
        this.complex = null;
    }

    public MetadataTypeItemDescriptor(String name, MetadataTypeDescriptor complex) {
        this.name = name;
        this.value = null;
        this.complex = complex;
    }
}
