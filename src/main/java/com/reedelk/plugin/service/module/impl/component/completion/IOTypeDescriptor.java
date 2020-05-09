package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.Collection;

public class IOTypeDescriptor {

    private final String type;
    private final Collection<IOTypeDTO> properties;

    public IOTypeDescriptor(String type, Collection<IOTypeDTO> properties) {
        this.type = type;
        this.properties = properties;
    }

    public String getType() {
        return type;
    }

    public Collection<IOTypeDTO> getProperties() {
        return properties;
    }
}
