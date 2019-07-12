package com.esb.plugin.component.scanner.property;

import com.esb.api.annotation.AutocompleteVariable;

public class ComponentWithAutocompleteVariable {

    @AutocompleteVariable(variableName = "input", initValue = "{}", contextName = "inputContext")
    @AutocompleteVariable(variableName = "output", initValue = "{}", contextName = "outputContext")
    private String script;

    public String getScript() {
        return script;
    }
}
