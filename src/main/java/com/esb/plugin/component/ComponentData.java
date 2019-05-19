package com.esb.plugin.component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ComponentData {

    private final ComponentDescriptor descriptor;

    private Map<String, Object> componentData = new HashMap<>();

    public ComponentData(final ComponentDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public Object get(String key) {
        return componentData.get(key);
    }

    public Object getOrDefault(String key, Object defaultValue) {
        return componentData.getOrDefault(key, defaultValue);
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

    public List<String> descriptorProperties() {
        return descriptor.getPropertiesNames();
    }

    public Optional<ComponentPropertyDescriptor> getPropertyDefinition(String propertyName) {
        return descriptor.getPropertyDefinition(propertyName);
    }

    public List<ComponentPropertyDescriptor> getComponentPropertyDescriptors() {
        return descriptor.getComponentPropertyDescriptors();
    }

    public TypeDescriptor getPropertyType(String propertyName) {
        return descriptor.getPropertyType(propertyName);
    }
}
