package com.esb.plugin.component.domain;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

public class ComponentData implements ComponentDataHolder {

    private final ComponentDescriptor descriptor;

    private Map<String, Object> componentData = new LinkedHashMap<>();

    public ComponentData(final ComponentDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public List<String> keys() {
        return new ArrayList<>(componentData.keySet());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key) {
        return (T) componentData.get(key);
    }

    @Override
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
        Collections.reverse(dataProperties);
        return dataProperties;
    }

    public Image getComponentImage() {
        return descriptor.getImage();
    }

    public Icon getComponentIcon() {
        return descriptor.getIcon();
    }

    public ComponentClass getComponentClass() {
        return descriptor.getComponentClass();
    }

    public List<ComponentPropertyDescriptor> getPropertiesDescriptors() {
        return descriptor.getPropertiesDescriptors();
    }

    public Optional<ComponentPropertyDescriptor> getPropertyDescriptor(String propertyName) {
        return descriptor.getPropertyDescriptor(propertyName);
    }
}
