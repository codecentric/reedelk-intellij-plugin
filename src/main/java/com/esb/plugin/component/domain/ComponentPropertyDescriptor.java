package com.esb.plugin.component.domain;

import static com.google.common.base.Preconditions.checkState;

public class ComponentPropertyDescriptor {

    public enum PropertyRequired {
        REQUIRED,
        NOT_REQUIRED;
    }

    private final PropertyRequired required;
    private final String displayName;
    private final String propertyName;
    private final Object defaultValue;
    private final TypeDescriptor propertyType;

    public ComponentPropertyDescriptor(
            final String propertyName,
            final TypeDescriptor propertyType,
            final String displayName) {
        this(propertyName, propertyType, displayName, null, PropertyRequired.NOT_REQUIRED);
    }

    public ComponentPropertyDescriptor(
            final String propertyName,
            final TypeDescriptor propertyType,
            final String displayName,
            final Object defaultValue) {
        this(propertyName, propertyType, displayName, defaultValue, PropertyRequired.NOT_REQUIRED);
    }

    public ComponentPropertyDescriptor(
            final String propertyName,
            final TypeDescriptor propertyType,
            final String displayName,
            final Object defaultValue,
            final PropertyRequired required) {
        checkState(propertyName != null, "property name");
        checkState(propertyType != null, "property type");
        this.required = required;
        this.displayName = displayName;
        this.propertyName = propertyName;
        this.defaultValue = defaultValue;
        this.propertyType = propertyType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public PropertyRequired required() {
        return required;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public TypeDescriptor getPropertyType() {
        return propertyType;
    }

}
