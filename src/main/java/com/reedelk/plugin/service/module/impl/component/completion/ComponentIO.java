package com.reedelk.plugin.service.module.impl.component.completion;

import java.util.Collection;
import java.util.List;

public class ComponentIO {

    private final IOTypeDescriptor attributes;
    private final List<IOTypeDescriptor> payload;

    public ComponentIO(IOTypeDescriptor attributes, List<IOTypeDescriptor> payload) {
        this.attributes = attributes;
        this.payload = payload;
    }

    public IOTypeDescriptor getAttributes() {
        return attributes;
    }

    public List<IOTypeDescriptor> getPayload() {
        return payload;
    }

    public static class IOTypeDTO {
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

    public static class IOTypeDescriptor {

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
}
