package com.reedelk.plugin.service.module.impl.component.scanner.property;

import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.service.module.impl.component.scanner.ComponentAnalyzerContext;
import com.reedelk.runtime.api.annotation.Property;
import io.github.classgraph.FieldInfo;

public class DisplayNameHandler implements Handler {

    @Override
    public void handle(FieldInfo propertyInfo, ComponentPropertyDescriptor.Builder builder, ComponentAnalyzerContext context) {
        String displayName =
                PropertyScannerUtils.getAnnotationValueOrDefault(propertyInfo, Property.class, propertyInfo.getName());
        if (Property.USE_DEFAULT_NAME.equals(displayName)) {
            String propertyName = propertyInfo.getName();
            builder.displayName(propertyName);
        } else {
            builder.displayName(displayName);
        }
    }
}
