package com.reedelk.plugin.component.domain;

public class WhenDefinition {

    private String propertyName;
    private String propertyValue;

    public WhenDefinition(String propertyName, String propertyValue) {
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getPropertyValue() {
        return propertyValue;
    }
}
