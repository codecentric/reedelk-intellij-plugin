package com.esb.plugin.component.scanner;

import java.util.Objects;

public class AutocompleteVariable {

    private String initValue;
    private String contextName;
    private String variableName;

    public AutocompleteVariable(String initValue, String contextName, String variableName) {
        this.initValue = initValue;
        this.contextName = contextName;
        this.variableName = variableName;
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
