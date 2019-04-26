package com.esb.plugin.designer.editor.component;

import java.awt.datatransfer.DataFlavor;
import java.util.*;

public class ComponentDescriptor {

    public static final DataFlavor FLAVOR = new DataFlavor(ComponentDescriptor.class, "Descriptor of Component");

    private String fullyQualifiedName;
    private String displayName;

    private List<String> propertiesNames = new ArrayList<>();
    private Map<String, Object> componentData = new HashMap<>();

    private ComponentDescriptor() {
    }

    public List<String> componentDataKeys() {
        return Collections.unmodifiableList(propertiesNames);
    }

    public Object getData(String key) {
        return componentData.get(key);
    }

    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getPropertiesNames() {
        return propertiesNames;
    }

    public void setPropertyValue(String propertyName, Object propertyValue) {
        this.componentData.put(propertyName, propertyValue);
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {

        private String displayName;
        private String fullyQualifiedName;
        private List<String> propertiesNames = new ArrayList<>();

        public Builder propertiesNames(List<String> propertiesNames) {
            this.propertiesNames.addAll(propertiesNames);
            return this;
        }

        public Builder fullyQualifiedName(String fullyQualifiedName) {
            this.fullyQualifiedName = fullyQualifiedName;
            return this;
        }

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public ComponentDescriptor build() {
            ComponentDescriptor descriptor = new ComponentDescriptor();
            descriptor.displayName = displayName;
            descriptor.fullyQualifiedName = fullyQualifiedName;
            descriptor.propertiesNames.addAll(propertiesNames);
            return descriptor;
        }

    }
}

