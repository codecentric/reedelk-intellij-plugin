package com.reedelk.plugin.commons;


import com.reedelk.module.descriptor.model.property.ScriptSignatureArgument;
import com.reedelk.module.descriptor.model.property.ScriptSignatureDescriptor;

import java.util.List;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static java.util.stream.Collectors.toList;

public class ScriptFunctionDefinitionBuilder {

    private ScriptFunctionDefinitionBuilder() {
    }

    public static String from(String scriptFunctionName, ScriptSignatureDescriptor signatureDefinition) {
        List<String> arguments = signatureDefinition.getArguments()
                .stream()
                .map(ScriptSignatureArgument::getArgumentName)
                .collect(toList());
        String scriptTemplateArguments = String.join(", ", arguments);
        return message("script.default.template", scriptFunctionName, scriptTemplateArguments, scriptFunctionName);
    }
}
