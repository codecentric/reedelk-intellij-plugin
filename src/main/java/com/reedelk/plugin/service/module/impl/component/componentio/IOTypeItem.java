package com.reedelk.plugin.service.module.impl.component.componentio;

public class IOTypeItem {

    public final String name;
    public final String value;
    public final IOTypeDescriptor complex;

    public IOTypeItem(String name, String value) {
        this.name = name;
        this.value = value;
        this.complex = null;
    }

    public IOTypeItem(String name, IOTypeDescriptor complex) {
        this.name = name;
        this.value = null;
        this.complex = complex;
    }
}
