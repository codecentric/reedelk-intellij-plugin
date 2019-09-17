package com.reedelk.plugin.component.scanner.property;

import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.scanner.ComponentAnalyzerContext;
import com.reedelk.runtime.api.annotation.Hidden;
import com.reedelk.runtime.api.annotation.Property;
import io.github.classgraph.FieldInfo;

import java.util.Optional;

public class ComponentPropertyAnalyzer {

    private final ComponentAnalyzerContext context;

    public ComponentPropertyAnalyzer(ComponentAnalyzerContext context) {
        this.context = context;
    }

    public Optional<ComponentPropertyDescriptor> analyze(FieldInfo fieldInfo) {
        return isVisibleProperty(fieldInfo) ?
                Optional.of(analyzeProperty(fieldInfo)) :
                Optional.empty();
    }

    private ComponentPropertyDescriptor analyzeProperty(FieldInfo propertyInfo) {
        return HandlersChain.descriptor(propertyInfo, context);
    }

    private boolean isVisibleProperty(FieldInfo fieldInfo) {
        return fieldInfo.hasAnnotation(Property.class.getName()) &&
                !fieldInfo.hasAnnotation(Hidden.class.getName());
    }
}
