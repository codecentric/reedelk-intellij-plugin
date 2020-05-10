package com.reedelk.plugin.service.module.impl.component.componentio;

import java.util.List;

public class IOComponent {

    private final IOTypeDescriptor attributes;
    private final List<IOTypeDescriptor> payload;

    public IOComponent(IOTypeDescriptor attributes, List<IOTypeDescriptor> payload) {
        this.attributes = attributes;
        this.payload = payload;
    }

    public IOTypeDescriptor getAttributes() {
        return attributes;
    }

    public List<IOTypeDescriptor> getPayload() {
        return payload;
    }

}
