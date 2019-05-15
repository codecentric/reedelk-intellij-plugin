package com.esb.plugin.component;

import com.esb.plugin.service.module.impl.PropertyDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentData {

    public static final String DESCRIPTION_PROPERTY_NAME = "description";

    private final ComponentDescriptor descriptor;

    private Map<String, Object> componentData = new HashMap<>();

    public ComponentData(final ComponentDescriptor descriptor) {
        this.descriptor = descriptor;
        this.componentData.put(DESCRIPTION_PROPERTY_NAME, descriptor.getDisplayName());
    }

    public Object get(String key) {
        return componentData.get(key);
    }

    public Object getOrDefault(String key, Object defaultValue) {
        Object value = componentData.get(key);
        return value == null ? defaultValue : value;
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

    public List<PropertyDefinition> getPropertyDefinitions() {
        return descriptor.getPropertyDefinitions();
    }

    public Class<?> getPropertyType(String propertyName) {
        return descriptor.getPropertyType(propertyName);
    }
}
