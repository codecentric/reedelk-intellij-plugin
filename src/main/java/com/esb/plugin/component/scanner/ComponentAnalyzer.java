package com.esb.plugin.component.scanner;

import com.esb.api.annotation.ESBComponent;
import com.esb.api.annotation.Hidden;
import com.esb.plugin.component.domain.ComponentDefaultDescriptor;
import com.esb.plugin.component.domain.ComponentDescriptor;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
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
        List<ComponentPropertyDescriptor> propertiesDescriptor = analyzeProperties(classInfo);
        return ComponentDefaultDescriptor.create()
                .fullyQualifiedName(classInfo.getName())
                .displayName(displayName)
                .paletteIcon(context.getIconByComponentQualifiedName(classInfo.getName()))
                .icon(context.getImageByComponentQualifiedName(classInfo.getName()))
                .hidden(isHidden(classInfo))
                .propertyDescriptors(propertiesDescriptor)
                .build();
    }

    private List<ComponentPropertyDescriptor> analyzeProperties(ClassInfo classInfo) {
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
