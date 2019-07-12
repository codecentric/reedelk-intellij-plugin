package com.esb.plugin.component.scanner.property;

import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.scanner.ComponentAnalyzerContext;
import io.github.classgraph.FieldInfo;

public class PropertyNameHandler implements Handler {

    @Override
    public void handle(FieldInfo propertyInfo, ComponentPropertyDescriptor.Builder builder, ComponentAnalyzerContext context) {
        String propertyName = propertyInfo.getName();
        builder.propertyName(propertyName);
    }
}
