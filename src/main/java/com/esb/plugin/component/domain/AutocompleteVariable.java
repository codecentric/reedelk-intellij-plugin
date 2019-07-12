package com.esb.plugin.component.domain;

import java.util.Objects;

public class AutocompleteVariable {

    private String initValue;
    private String contextName;
    private String variableName;

    public AutocompleteVariable(String variableName, String initValue, String contextName) {
        this.initValue = initValue;
        this.contextName = contextName;
        this.variableName = variableName;
    }

    public String getInitValue() {
        return initValue;
    }

    public String getContextName() {
        return contextName;
    }

    public String getVariableName() {
        return variableName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AutocompleteVariable that = (AutocompleteVariable) o;
        return initValue.equals(that.initValue) &&
                contextName.equals(that.contextName) &&
                variableName.equals(that.variableName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(initValue, contextName, variableName);
    }
}
