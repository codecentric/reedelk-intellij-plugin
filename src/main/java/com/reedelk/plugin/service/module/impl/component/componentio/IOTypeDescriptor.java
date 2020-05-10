package com.reedelk.plugin.service.module.impl.component.componentio;

import java.util.Collection;

public class IOTypeDescriptor {

    private final String type;
    private final Collection<IOTypeItem> properties;

    public IOTypeDescriptor(String type, Collection<IOTypeItem> properties) {
        this.type = type;
        this.properties = properties;
    }

    public String getType() {
        return type;
    }

    public Collection<IOTypeItem> getProperties() {
        return properties;
    }
}
