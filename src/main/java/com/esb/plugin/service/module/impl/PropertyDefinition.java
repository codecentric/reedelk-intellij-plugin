package com.esb.plugin.service.module.impl;

import static com.google.common.base.Preconditions.checkState;

public class PropertyDefinition {

    private final String propertyName;
    private final Class<?> propertyType;

    public PropertyDefinition(final String propertyName, final Class<?> propertyType) {
        checkState(propertyName != null, "propertyName");
        checkState(propertyType != null, "propertyType");
        this.propertyName = propertyName;
        this.propertyType = propertyType;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Class<?> getPropertyType() {
        return propertyType;
    }
}
