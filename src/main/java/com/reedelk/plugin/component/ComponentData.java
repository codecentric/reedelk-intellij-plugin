package com.reedelk.plugin.component;

import com.reedelk.module.descriptor.model.ComponentDataHolder;
import com.reedelk.module.descriptor.model.ComponentDescriptor;
import com.reedelk.module.descriptor.model.ComponentType;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.plugin.commons.Images;

import java.awt.*;
import java.util.List;
import java.util.*;

public class ComponentData implements ComponentDataHolder {

    private final ComponentDescriptor descriptor;

    private Map<String, Object> propertyNameDataMap = new LinkedHashMap<>();

    public ComponentData(final ComponentDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public List<String> keys() {
        return new ArrayList<>(propertyNameDataMap.keySet());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key) {
        return (T) propertyNameDataMap.get(key);
    }

    @Override
    public void set(String propertyName, Object propertyValue) {
        propertyNameDataMap.put(propertyName, propertyValue);
    }

    @Override
    public boolean has(String key) {
        return propertyNameDataMap.containsKey(key);
    }

    public String getFullyQualifiedName() {
        return descriptor.getFullyQualifiedName();
    }

    public String getDisplayName() {
        return descriptor.getDisplayName();
    }

    public List<String> getDataProperties() {
        List<String> dataProperties = new ArrayList<>();
        for (Map.Entry<String, Object> entry : propertyNameDataMap.entrySet()) {
            dataProperties.add(entry.getKey());
        }
        Collections.reverse(dataProperties);
        return dataProperties;
    }

    public Image getComponentImage() {
        return Optional.ofNullable(descriptor.getImage())
                .orElse(Images.Component.DefaultComponent);
    }

    public ComponentType getComponentClass() {
        return descriptor.getType();
    }

    public List<PropertyDescriptor> getPropertiesDescriptors() {
        return descriptor.getProperties();
    }

    public Optional<PropertyDescriptor> getPropertyDescriptor(String propertyName) {
        return descriptor.getPropertyDescriptor(propertyName);
    }
}
