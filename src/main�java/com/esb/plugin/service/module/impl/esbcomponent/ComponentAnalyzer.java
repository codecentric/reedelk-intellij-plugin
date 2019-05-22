package com.esb.plugin.service.module.impl.esbcomponent;

import com.esb.api.annotation.ESBComponent;
import com.esb.api.annotation.Hidden;
import com.esb.plugin.component.ComponentDescriptor;
import com.esb.plugin.component.ComponentPropertyDescriptor;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationParameterValueList;
import io.github.classgraph.ClassInfo;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class ComponentAnalyzer {

    private final ComponentAnalyzerContext context;
    private final ComponentPropertyAnalyzer propertyAnalyzer;

    public ComponentAnalyzer(ComponentAnalyzerContext context) {
        this.propertyAnalyzer = new ComponentPropertyAnalyzer(context);
        this.context = context;
    }

    public ComponentDescriptor analyze(ClassInfo classInfo) {
        String displayName = getComponentDisplayName(classInfo);
        return ComponentDescriptor.create()
                .fullyQualifiedName(classInfo.getName())
                .displayName(displayName)
                .paletteIcon(context.getIconByClassName(classInfo.getName()))
                .icon(context.getImageByClassName(classInfo.getName()))
                .hidden(isHidden(classInfo))
                .propertyDefinitions(mapPropertyDefinitions(classInfo))
                .build();
    }

    private List<ComponentPropertyDescriptor> mapPropertyDefinitions(ClassInfo classInfo) {
        return classInfo
                .getFieldInfo()
                .stream()
                .map(propertyAnalyzer::analyze)
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

    private boolean isHidden(ClassInfo componentClassInfo) {
        return componentClassInfo.hasAnnotation(Hidden.class.getName());
    }

}
