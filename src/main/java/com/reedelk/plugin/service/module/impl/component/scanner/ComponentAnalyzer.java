package com.reedelk.plugin.service.module.impl.component.scanner;

import com.reedelk.plugin.component.domain.ComponentClass;
import com.reedelk.plugin.component.domain.ComponentDefaultDescriptor;
import com.reedelk.plugin.component.domain.ComponentDescriptor;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.service.module.impl.component.scanner.property.ComponentPropertyAnalyzer;
import com.reedelk.runtime.api.annotation.ESBComponent;
import com.reedelk.runtime.api.annotation.Hidden;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationParameterValueList;
import io.github.classgraph.ClassInfo;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

class ComponentAnalyzer {

    private final ComponentPropertyAnalyzer propertyAnalyzer;
    private final ComponentAnalyzerContext context;

    ComponentAnalyzer(ComponentAnalyzerContext context, ComponentPropertyAnalyzer propertyAnalyzer) {
        this.propertyAnalyzer = propertyAnalyzer;
        this.context = context;
    }

    ComponentDescriptor analyze(ClassInfo classInfo) {
        String displayName = getComponentDisplayName(classInfo);
        ComponentClass componentClass = getComponentClass(classInfo);
        List<ComponentPropertyDescriptor> propertiesDescriptor = analyzeProperties(classInfo);
        return ComponentDefaultDescriptor.create()
                .displayName(displayName)
                .hidden(isHidden(classInfo))
                .componentClass(componentClass)
                .fullyQualifiedName(classInfo.getName())
                .propertyDescriptors(propertiesDescriptor)
                .icon(context.getImageByComponentQualifiedName(classInfo.getName()))
                .paletteIcon(context.getIconByComponentQualifiedName(classInfo.getName()))
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
        // The ClassInfo component descriptor *must* have the ESBComponent annotation if we get here.
        AnnotationInfo componentDisplayName = componentClassInfo.getAnnotationInfo(ESBComponent.class.getName());
        AnnotationParameterValueList parameterValues = componentDisplayName.getParameterValues();
        return parameterValues.containsName("value") ?
                (String) parameterValues.getValue("value") :
                componentClassInfo.getSimpleName();
    }

    private boolean isHidden(ClassInfo componentClassInfo) {
        return componentClassInfo.hasAnnotation(Hidden.class.getName());
    }

    private ComponentClass getComponentClass(ClassInfo classInfo) {
        ComponentClassAnalyzer analyzer = new ComponentClassAnalyzer(classInfo);
        return analyzer.analyze();
    }
}
