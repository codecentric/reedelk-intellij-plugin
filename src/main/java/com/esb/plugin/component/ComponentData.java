package com.esb.plugin.component;

import java.util.*;

public class ComponentData {

    private final ComponentDescriptor descriptor;

    private Map<String, Object> componentData = new LinkedHashMap<>();

    public ComponentData(final ComponentDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public Object get(String key) {
        return componentData.get(key);
    }

    public void set(String propertyName, Object propertyValue) {
        componentData.put(propertyName, propertyValue);
    }

    public String getFullyQualifiedName() {
        return descriptor.getFullyQualifiedName();
    }

    public String getDisplayName() {
        return descriptor.getDisplayName();
    }

    public List<String> getDataProperties() {
        List<String> dataProperties = new ArrayList<>();
        for (Map.Entry<String, Object> entry : componentData.entrySet()) {
            dataProperties.add(entry.getKey());
        }
        return dataProperties;
    }

    public List<ComponentPropertyDescriptor> getPropertiesDescriptors() {
        return descriptor.getPropertiesDescriptors();
    }

    public Optional<ComponentPropertyDescriptor> getPropertyDescriptor(String propertyName) {
        return descriptor.getPropertyDescriptor(propertyName);
    }

}
