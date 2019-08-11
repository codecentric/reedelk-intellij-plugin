package com.reedelk.plugin.component.domain;

public class VariableDefinition {

    private String contextName;
    private String variableName;

    public VariableDefinition(String variableName, String contextName) {
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
