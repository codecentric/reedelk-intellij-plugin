package com.reedelk.plugin.service.module.impl.completion;

import java.util.Map;

public class ComponentIO {

    private final Map<String, IOTypeDescriptor> attributes;
    private final Map<String, IOTypeDescriptor> payload;

    public ComponentIO(Map<String, IOTypeDescriptor> attributes, Map<String, IOTypeDescriptor> payload) {
        this.attributes = attributes;
        this.payload = payload;
    }

    public Map<String, IOTypeDescriptor> getPayload() {
        return payload;
    }

    public Map<String, IOTypeDescriptor> getAttributes() {
        return attributes;
    }

    public static class IOTypeDescriptor {

        private final boolean nameOnly;
        private final String name;
        private final Map<String, IOTypeDescriptor> typeProperties;

        private IOTypeDescriptor(String name) {
            this.nameOnly = true;
            this.name = name;
            this.typeProperties = null;
        }

        private IOTypeDescriptor(String name, Map<String, IOTypeDescriptor> typeProperties) {
            this.nameOnly = false;
            this.name = name;
            this.typeProperties = typeProperties;
        }

        public static IOTypeDescriptor create(String name) {
            return new IOTypeDescriptor(name);
        }

        public static IOTypeDescriptor create(String name, Map<String, IOTypeDescriptor> typeProperties) {
            return new IOTypeDescriptor(name, typeProperties);
        }

        public boolean isNameOnly() {
            return nameOnly;
        }

        public String getName() {
            return name;
        }

        public Map<String, IOTypeDescriptor> getTypeProperties() {
            return typeProperties;
        }
    }
}
