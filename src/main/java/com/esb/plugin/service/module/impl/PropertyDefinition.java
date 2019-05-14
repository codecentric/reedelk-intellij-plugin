package com.esb.plugin.service.module.impl;

import static com.google.common.base.Preconditions.checkState;

public class PropertyDefinition {

    private final boolean required;
    private final String displayName;
    private final String propertyName;
    private final Class<?> propertyType;
    private final Object defaultValue;

    public PropertyDefinition(
            final String propertyName,
            final Class<?> propertyType,
            final boolean required,
            final Object defaultValue,
            final String displayName) {

        checkState(propertyName != null, "propertyName");
        checkState(propertyType != null, "propertyType");
        this.required = required;
        this.displayName = displayName;
        this.propertyName = propertyName;
        this.propertyType = propertyType;
        this.defaultValue = defaultValue;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Class<?> getPropertyType() {
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
