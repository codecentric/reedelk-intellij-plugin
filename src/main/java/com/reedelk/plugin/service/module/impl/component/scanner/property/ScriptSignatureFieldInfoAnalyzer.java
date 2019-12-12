package com.reedelk.plugin.service.module.impl.component.scanner.property;

import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.ScriptSignatureDefinition;
import com.reedelk.plugin.service.module.impl.component.scanner.ComponentAnalyzerContext;
import com.reedelk.plugin.service.module.impl.component.scanner.ScannerUtil;
import com.reedelk.runtime.api.annotation.ScriptSignature;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.FieldInfo;

import java.util.List;

public class ScriptSignatureFieldInfoAnalyzer implements FieldInfoAnalyzer {

    @Override
    public void handle(FieldInfo propertyInfo, ComponentPropertyDescriptor.Builder builder, ComponentAnalyzerContext context) {
        if (ScannerUtil.hasAnnotation(propertyInfo, ScriptSignature.class)) {
            AnnotationInfo info = propertyInfo.getAnnotationInfo(ScriptSignature.class.getName());
            List<String> functionSignatureArguments =
                    ScannerUtil.stringListParameterValueFrom(info, "arguments");
            ScriptSignatureDefinition definition =
                    new ScriptSignatureDefinition(functionSignatureArguments);
            builder.scriptSignature(definition);
        }
    }
}
