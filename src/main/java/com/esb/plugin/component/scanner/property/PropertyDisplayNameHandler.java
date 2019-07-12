package com.esb.plugin.component.scanner.property;

import com.esb.api.annotation.Property;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.scanner.ComponentAnalyzerContext;
import io.github.classgraph.FieldInfo;

public class PropertyDisplayNameHandler implements Handler {

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
