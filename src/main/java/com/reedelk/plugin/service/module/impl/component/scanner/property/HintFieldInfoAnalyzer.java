package com.reedelk.plugin.service.module.impl.component.scanner.property;

import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.service.module.impl.component.scanner.ComponentAnalyzerContext;
import com.reedelk.runtime.api.annotation.Hint;
import io.github.classgraph.FieldInfo;

public class HintFieldInfoAnalyzer implements FieldInfoAnalyzer {
    @Override
    public void handle(FieldInfo propertyInfo, ComponentPropertyDescriptor.Builder builder, ComponentAnalyzerContext context) {
        String hintValue =
                PropertyScannerUtils.getAnnotationValueOrDefault(propertyInfo, Hint.class, null);
        builder.hintValue(hintValue);
    }
}
