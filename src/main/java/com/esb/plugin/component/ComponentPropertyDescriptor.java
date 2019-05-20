package com.esb.plugin.component;

import static com.google.common.base.Preconditions.checkState;

public class ComponentPropertyDescriptor {

    private final boolean required;
    private final String displayName;
    private final String propertyName;
    private final Object defaultValue;
    private final TypeDescriptor propertyType;

    public ComponentPropertyDescriptor(
            final String propertyName,
            final TypeDescriptor propertyType,
            final String displayName,
            final Object defaultValue,
            final boolean required) {

        checkState(propertyName != null, "property name");
        checkState(propertyType != null, "property type");
        this.required = required;
        this.displayName = displayName;
        this.propertyName = propertyName;
        this.defaultValue = defaultValue;
        this.propertyType = propertyType;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public TypeDescriptor getPropertyType() {
        return propertyType;
    }

    public boolean isRequired() {
        return required;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
}
