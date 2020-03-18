package com.reedelk.plugin.component.type.generic;

import com.reedelk.runtime.api.component.Implementor;

public class CustomMapValueType implements Implementor {

    private String stringProperty;
    private Integer integerObjectProperty;

    public String getStringProperty() {
        return stringProperty;
    }

    public void setStringProperty(String stringProperty) {
        this.stringProperty = stringProperty;
    }

    public Integer getIntegerObjectProperty() {
        return integerObjectProperty;
    }

    public void setIntegerObjectProperty(Integer integerObjectProperty) {
        this.integerObjectProperty = integerObjectProperty;
    }
}
