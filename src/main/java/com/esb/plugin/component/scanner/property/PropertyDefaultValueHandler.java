package com.esb.plugin.component.scanner.property;

import com.esb.api.annotation.Default;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.scanner.ComponentAnalyzerContext;
import io.github.classgraph.FieldInfo;

public class PropertyDefaultValueHandler implements Handler {

    @Override
    public void handle(FieldInfo propertyInfo, ComponentPropertyDescriptor.Builder builder, ComponentAnalyzerContext context) {
        String stringValue =
                PropertyScannerUtils.getAnnotationValueOrDefault(propertyInfo, Default.class, Default.USE_DEFAULT_VALUE);
        builder.defaultValue(stringValue);
    }
}
