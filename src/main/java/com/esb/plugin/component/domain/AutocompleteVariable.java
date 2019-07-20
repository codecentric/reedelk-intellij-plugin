package com.esb.plugin.component.domain;

public class AutocompleteVariable {

    private String contextName;
    private String variableName;

    public AutocompleteVariable(String variableName, String contextName) {
        this.contextName = contextName;
        this.variableName = variableName;
    }

    public String getContextName() {
        return contextName;
    }

    public String getVariableName() {
        return variableName;
    }

}
