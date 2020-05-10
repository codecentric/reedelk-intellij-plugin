package com.reedelk.plugin.service.module.impl.component.componentio;

import java.util.List;

public class IOComponent {

    private final String payloadDescription;
    private final IOTypeDescriptor attributes;
    private final List<IOTypeDescriptor> payload;

    public IOComponent(IOTypeDescriptor attributes, List<IOTypeDescriptor> payload, String payloadDescription) {
        this.payload = payload;
        this.attributes = attributes;
        this.payloadDescription = payloadDescription;
    }

    public IOTypeDescriptor getAttributes() {
        return attributes;
    }

    public List<IOTypeDescriptor> getPayload() {
        return payload;
    }

    public String getPayloadDescription() {
        return payloadDescription;
    }
}
