package com.reedelk.plugin.component.scanner.property;

import com.reedelk.runtime.api.annotation.Variable;

public class ComponentWithAutocompleteVariables {

    @Variable(variableName = "input", contextName = "inputContext")
    @Variable(variableName = "output", contextName = "outputContext")
    private String script;

}
