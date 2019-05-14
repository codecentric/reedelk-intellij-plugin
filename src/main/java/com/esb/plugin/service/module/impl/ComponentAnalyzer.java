package com.esb.plugin.service.module.impl;

import com.esb.api.annotation.ESBComponent;
import com.esb.plugin.component.ComponentDescriptor;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationParameterValueList;
import io.github.classgraph.ClassInfo;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class ComponentAnalyzer {

    private PropertyDefinitionAnalyzer propertyAnalyzer = new PropertyDefinitionAnalyzer();

    public ComponentDescriptor analyze(ClassInfo classInfo) {
        String displayName = getComponentDisplayName(classInfo);
        return ComponentDescriptor.create()
                .fullyQualifiedName(classInfo.getName())
                .displayName(displayName)
                .propertyDefinitions(mapPropertyDefinitions(classInfo))
                .build();
    }

    private List<PropertyDefinition> mapPropertyDefinitions(ClassInfo classInfo) {
        return classInfo
                .getFieldInfo()
                .stream()
                .map(fieldInfo -> propertyAnalyzer.analyze(fieldInfo))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
    }

    private String getComponentDisplayName(ClassInfo componentClassInfo) {
        if (componentClassInfo.hasAnnotation(ESBComponent.class.getName())) {
            AnnotationInfo componentDisplayName = componentClassInfo.getAnnotationInfo(ESBComponent.class.getName());
            AnnotationParameterValueList parameterValues = componentDisplayName.getParameterValues();
            return (String) parameterValues.getValue("value");
        } else {
            return componentClassInfo.getSimpleName();
        }
    }


}
