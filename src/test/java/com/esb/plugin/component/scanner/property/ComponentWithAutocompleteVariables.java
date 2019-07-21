package com.esb.plugin.component.scanner.property;

import com.esb.api.annotation.Variable;

public class ComponentWithAutocompleteVariables {

    @Variable(variableName = "input", contextName = "inputContext")
    @Variable(variableName = "output", contextName = "outputContext")
    private String script;

}
