package com.esb.plugin.component.scanner.property;

import com.esb.api.annotation.Required;
import com.esb.plugin.component.scanner.ComponentAnalyzerContext;
import io.github.classgraph.FieldInfo;

import static com.esb.plugin.component.domain.ComponentPropertyDescriptor.Builder;
import static com.esb.plugin.component.domain.ComponentPropertyDescriptor.PropertyRequired;

public class PropertyRequiredHandler implements Handler {

    @Override
    public void handle(FieldInfo propertyInfo, Builder builder, ComponentAnalyzerContext context) {
        PropertyRequired required = propertyInfo.hasAnnotation(Required.class.getName()) ?
                PropertyRequired.REQUIRED : PropertyRequired.NOT_REQUIRED;
        builder.required(required);
    }
}
