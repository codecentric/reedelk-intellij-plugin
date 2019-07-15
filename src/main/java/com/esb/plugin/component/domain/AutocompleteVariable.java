package com.esb.plugin.component.domain;

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

}
