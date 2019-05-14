package com.esb.plugin.component;

import com.esb.plugin.service.module.impl.PropertyDefinition;

import java.awt.datatransfer.DataFlavor;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * An object describing the component to be added to the graph.
 */
public class ComponentDescriptor {

    public static final DataFlavor FLAVOR = new DataFlavor(ComponentDescriptor.class, "Descriptor of ComponentData");

    private String fullyQualifiedName;
    private String displayName;

    private List<PropertyDefinition> propertyDefinitions = new ArrayList<>();

    private ComponentDescriptor() {
    }

    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Optional<PropertyDefinition> getPropertyDefinition(String propertyName) {
        return propertyDefinitions
                .stream()
                .filter(propertyDefinition -> propertyDefinition.getPropertyName().equals(propertyName))
                .findFirst();
    }

    public List<String> getPropertiesNames() {
        return propertyDefinitions.stream()
                .map(PropertyDefinition::getPropertyName)
                .collect(toList());
    }

    public static Builder create() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComponentDescriptor that = (ComponentDescriptor) o;
        return fullyQualifiedName.equals(that.fullyQualifiedName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullyQualifiedName);
    }

    public Class<?> getPropertyType(String propertyName) {
        return propertyDefinitions.stream()
                .filter(propertyDefinition -> propertyDefinition.getPropertyName().equals(propertyName))
                .findFirst()
                .orElseThrow(() -> {
                    throw new IllegalStateException("Property not found");
                }).getPropertyType();
    }


    public static class Builder {

        private String displayName;
        private String fullyQualifiedName;
        private List<PropertyDefinition> propertyDefinitions = new ArrayList<>();

        public Builder propertyDefinitions(List<PropertyDefinition> propertyDefinitions) {
            this.propertyDefinitions.addAll(propertyDefinitions);
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
            descriptor.propertyDefinitions.addAll(propertyDefinitions);
            return descriptor;
        }
    }

}

