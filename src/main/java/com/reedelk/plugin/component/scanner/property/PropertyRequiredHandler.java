package com.reedelk.plugin.component.scanner.property;

import com.reedelk.plugin.component.scanner.ComponentAnalyzerContext;
import com.reedelk.runtime.api.annotation.Required;
import io.github.classgraph.FieldInfo;

import static com.reedelk.plugin.component.domain.ComponentPropertyDescriptor.Builder;
import static com.reedelk.plugin.component.domain.ComponentPropertyDescriptor.PropertyRequired;

public class PropertyRequiredHandler implements Handler {

    @Override
    public void handle(FieldInfo propertyInfo, Builder builder, ComponentAnalyzerContext context) {
        PropertyRequired required = propertyInfo.hasAnnotation(Required.class.getName()) ?
                PropertyRequired.REQUIRED : PropertyRequired.NOT_REQUIRED;
        builder.required(required);
    }
}
