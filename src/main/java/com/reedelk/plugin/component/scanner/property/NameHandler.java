package com.reedelk.plugin.component.scanner.property;

import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.scanner.ComponentAnalyzerContext;
import io.github.classgraph.FieldInfo;

public class NameHandler implements Handler {

    @Override
    public void handle(FieldInfo propertyInfo, ComponentPropertyDescriptor.Builder builder, ComponentAnalyzerContext context) {
        String propertyName = propertyInfo.getName();
        builder.propertyName(propertyName);
    }
}
