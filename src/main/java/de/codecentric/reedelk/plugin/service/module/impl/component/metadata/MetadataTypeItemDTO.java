package de.codecentric.reedelk.plugin.service.module.impl.component.metadata;

public class MetadataTypeItemDTO {

    public final String name;
    public final String displayType;
    public final MetadataTypeDTO complex;

    public MetadataTypeItemDTO(String name, String displayType) {
        this.name = name;
        this.displayType = displayType;
        this.complex = null;
    }

    public MetadataTypeItemDTO(String name, MetadataTypeDTO complex) {
        this.name = name;
        this.displayType = null;
        this.complex = complex;
    }
}
