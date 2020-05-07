package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.Collection;
import java.util.List;

public class ComponentIO {

    private final OutputDescriptor attributes;
    private final List<OutputDescriptor> payload;

    public ComponentIO(OutputDescriptor attributes, List<OutputDescriptor> payload) {
        this.attributes = attributes;
        this.payload = payload;
    }

    public OutputDescriptor getAttributes() {
        return attributes;
    }

    public List<OutputDescriptor> getPayload() {
        return payload;
    }

    public static class OutputDescriptor {

        private final String type;
        private final Collection<Suggestion> properties;

        public OutputDescriptor(String type) {
            this.type = type;
            this.properties = null;
        }

        public OutputDescriptor(String type, Collection<Suggestion> properties) {
            this.type = type;
            this.properties = properties;
        }

        public String getType() {
            return type;
        }

        public Collection<Suggestion> getProperties() {
            return properties;
        }
    }
}
