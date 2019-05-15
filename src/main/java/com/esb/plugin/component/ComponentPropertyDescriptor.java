package com.esb.plugin.component;

import static com.google.common.base.Preconditions.checkState;

public class ComponentPropertyDescriptor {

    private final boolean required;
    private final String displayName;
    private final String propertyName;
    private final Object defaultValue;
    private final PropertyPrimitiveTypeDescriptor propertyTypeDescriptor;

    public ComponentPropertyDescriptor(
            final String propertyName,
            final String displayName,
            final boolean required,
            final Object defaultValue,
            final PropertyPrimitiveTypeDescriptor propertyTypeDescriptor) {

        checkState(propertyName != null, "property name");
        checkState(propertyTypeDescriptor != null, "property type descriptor");
        this.required = required;
        this.displayName = displayName;
        this.propertyName = propertyName;
        this.defaultValue = defaultValue;
        this.propertyTypeDescriptor = propertyTypeDescriptor;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public PropertyPrimitiveTypeDescriptor getPropertyType() {
        return propertyTypeDescriptor;
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
