package com.reedelk.plugin.service.module.impl.component.completion;

public class IOTypeDTO {
    public final String name;
    public final String value;
    public final IOTypeDescriptor complex;

    public IOTypeDTO(String name, String value) {
        this.name = name;
        this.value = value;
        this.complex = null;
    }

    public IOTypeDTO(String name, IOTypeDescriptor complex) {
        this.name = name;
        this.value = null;
        this.complex = complex;
    }
}
