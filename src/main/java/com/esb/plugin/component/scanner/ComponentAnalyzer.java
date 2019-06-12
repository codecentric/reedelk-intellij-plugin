package com.esb.plugin.component.scanner;

import com.esb.api.annotation.ESBComponent;
import com.esb.api.annotation.Hidden;
import com.esb.api.component.AbstractInbound;
import com.esb.api.component.Inbound;
import com.esb.plugin.component.domain.ComponentDefaultDescriptor;
import com.esb.plugin.component.domain.ComponentDescriptor;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationParameterValueList;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class ComponentAnalyzer {

    private final ComponentAnalyzerContext context;
    private final ComponentPropertyAnalyzer propertyAnalyzer;

    ComponentAnalyzer(ComponentAnalyzerContext context, ComponentPropertyAnalyzer propertyAnalyzer) {
        this.propertyAnalyzer = propertyAnalyzer;
        this.context = context;
    }

    public ComponentDescriptor analyze(ClassInfo classInfo) {
        String displayName = getComponentDisplayName(classInfo);
        boolean isInbound = isInbound(classInfo);
        List<ComponentPropertyDescriptor> propertiesDescriptor = analyzeProperties(classInfo);
        return ComponentDefaultDescriptor.create()
                .inbound(isInbound)
                .displayName(displayName)
                .hidden(isHidden(classInfo))
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

    /**
     * A component is inbound if implements either the AbstractInbound abstract class
     * or Inbound interface.
     *
     * @param componentClassInfo the class info descriptor.
     * @return true if this class descriptor relates to an an Inbound component, false otherwise.
     */
    private boolean isInbound(ClassInfo componentClassInfo) {
        ClassInfoList superclasses = componentClassInfo.getSuperclasses();
        boolean implementsAbstractInbound = superclasses.stream().anyMatch(classInfo ->
                classInfo.getName().equals(AbstractInbound.class.getName()));
        boolean implementsInboundInterface = componentClassInfo.getInterfaces().stream().anyMatch(classInfo ->
                classInfo.getName().equals(Inbound.class.getName()));
        return implementsAbstractInbound || implementsInboundInterface;
    }

}
