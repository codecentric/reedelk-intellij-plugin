package com.reedelk.plugin.component.scanner.property;

import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.scanner.ComponentAnalyzerContext;
import com.reedelk.runtime.api.annotation.Hint;
import io.github.classgraph.FieldInfo;

public class HintHandler implements Handler {
    @Override
    public void handle(FieldInfo propertyInfo, ComponentPropertyDescriptor.Builder builder, ComponentAnalyzerContext context) {
        String hintValue =
                PropertyScannerUtils.getAnnotationValueOrDefault(propertyInfo, Hint.class, null);
        builder.hintValue(hintValue);
    }
}