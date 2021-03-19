package de.codecentric.reedelk.plugin.component;

import de.codecentric.reedelk.plugin.commons.Images;
import de.codecentric.reedelk.module.descriptor.model.component.ComponentDataHolder;
import de.codecentric.reedelk.module.descriptor.model.component.ComponentDescriptor;
import de.codecentric.reedelk.module.descriptor.model.component.ComponentType;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyDescriptor;

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

    public String getDescription() {
        return descriptor.getDescription();
    }

    public String getDisplayName() {
        return descriptor.getDisplayName();
    }

    public Image getComponentImage() {
        return Optional.ofNullable(descriptor.getImage())
                .orElse(Images.Component.DefaultComponent);
    }

    public String getFullyQualifiedName() {
        return descriptor.getFullyQualifiedName();
    }

    public List<String> getDataProperties() {
        List<String> dataProperties = new ArrayList<>();
        for (Map.Entry<String, Object> entry : propertyNameDataMap.entrySet()) {
            dataProperties.add(entry.getKey());
        }
        Collections.reverse(dataProperties);
        return dataProperties;
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
