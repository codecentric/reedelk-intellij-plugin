package com.esb.plugin.component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentData {

    public static final String DESCRIPTION_PROPERTY_NAME = "description";

    private final ComponentDescriptor descriptor;

    private Map<String, Object> componentData = new HashMap<>();

    public ComponentData(final ComponentDescriptor descriptor) {
        this.descriptor = descriptor;
        // TODO: This is wrong here.... should be external, where this gets created.
        // TODO: Also the description property should be added to the descriptor as well.
        this.componentData.put(DESCRIPTION_PROPERTY_NAME, descriptor.getDisplayName());
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

    public List<ComponentPropertyDescriptor> getComponentPropertyDescriptors() {
        return descriptor.getComponentPropertyDescriptors();
    }

    public PropertyTypeDescriptor getPropertyType(String propertyName) {
        return descriptor.getPropertyType(propertyName);
    }
}
