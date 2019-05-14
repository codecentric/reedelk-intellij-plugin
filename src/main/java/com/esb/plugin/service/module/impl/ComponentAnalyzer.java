package com.esb.plugin.service.module.impl;

import com.esb.plugin.component.ComponentDescriptor;
import io.github.classgraph.ClassInfo;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class ComponentAnalyzer {

    private SetterPropertyDefinitionMapper mapper = new SetterPropertyDefinitionMapper();

    public ComponentDescriptor analyze(ClassInfo classInfo) {
        return buildDescriptorFromClassInfo(classInfo);
    }

    private ComponentDescriptor buildDescriptorFromClassInfo(ClassInfo classInfo) {
        return ComponentDescriptor.create()
                .fullyQualifiedName(classInfo.getName())
                .displayName(classInfo.getSimpleName())
                .propertyDefinitions(mapPropertyDefinitions(classInfo))
                .build();
    }

    private List<PropertyDefinition> mapPropertyDefinitions(ClassInfo classInfo) {
        return classInfo.getMethodInfo().stream()
                .filter(methodInfo -> methodInfo.getName().startsWith("set"))
                .map(methodInfo -> mapper.map(methodInfo))
                .collect(toList());
    }
}
