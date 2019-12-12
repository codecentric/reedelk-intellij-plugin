package com.reedelk.plugin.commons;

import com.reedelk.plugin.component.domain.ScriptSignatureDefinition;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class ScriptFunctionDefinitionBuilder {

    private ScriptFunctionDefinitionBuilder() {
    }

    public static String from(String scriptFunctionName, ScriptSignatureDefinition signatureDefinition) {
        String scriptTemplateArguments = String.join(",", signatureDefinition.getArguments());
        return message("script.default.template", scriptFunctionName, scriptTemplateArguments, scriptFunctionName);
    }
}
