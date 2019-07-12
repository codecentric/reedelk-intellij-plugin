package com.esb.plugin.component.scanner.property;

import com.esb.api.annotation.Property;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.scanner.ComponentAnalyzerContext;
import io.github.classgraph.FieldInfo;

import java.util.Optional;

public class ComponentPropertyAnalyzer {

    private final ComponentAnalyzerContext context;

    public ComponentPropertyAnalyzer(ComponentAnalyzerContext context) {
        this.context = context;
    }

    public Optional<ComponentPropertyDescriptor> analyze(FieldInfo fieldInfo) {
        return fieldInfo.hasAnnotation(Property.class.getName()) ?
                Optional.of(analyzeProperty(fieldInfo)) :
                Optional.empty();
    }

    private ComponentPropertyDescriptor analyzeProperty(FieldInfo propertyInfo) {
        return HandlersChain.descriptor(propertyInfo, context);
    }
}
