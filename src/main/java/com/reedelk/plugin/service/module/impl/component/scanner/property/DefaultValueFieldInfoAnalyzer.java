package com.reedelk.plugin.service.module.impl.component.scanner.property;

import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.service.module.impl.component.scanner.ComponentAnalyzerContext;
import com.reedelk.runtime.api.annotation.Default;
import io.github.classgraph.FieldInfo;

public class DefaultValueFieldInfoAnalyzer implements FieldInfoAnalyzer {

    @Override
    public void handle(FieldInfo propertyInfo, ComponentPropertyDescriptor.Builder builder, ComponentAnalyzerContext context) {
        String stringValue =
                PropertyScannerUtils.getAnnotationValueOrDefault(propertyInfo, Default.class, Default.USE_DEFAULT_VALUE);
        builder.defaultValue(stringValue);
    }
}
