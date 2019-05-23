package com.esb.plugin.service.module.impl.esbcomponent;

import com.esb.api.annotation.*;
import com.esb.plugin.component.ComponentDescriptor;
import com.esb.plugin.component.ComponentPropertyDescriptor;
import com.esb.plugin.component.PrimitiveTypeDescriptor;
import com.esb.plugin.component.TypeDescriptor;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;

import java.util.List;
import java.util.stream.Collectors;

public class SourceComponentScanner {


    private final String sourcePath;

    public SourceComponentScanner(final String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public List<ComponentDescriptor> analyze() {
        SpoonAPI spoon = new Launcher();
        spoon.addInputResource(sourcePath);
        CtModel classModel = spoon.buildModel();
        return classModel.getAllTypes()
                .stream()
                .filter(this::isESBComponent)
                .map(this::buildDescriptor)
                .collect(Collectors.toList());
    }

    private ComponentDescriptor buildDescriptor(CtType<?> type) {
        String displayName = type.getAnnotation(ESBComponent.class).value();
        return ComponentDescriptor.create()
                .displayName(displayName)
                .fullyQualifiedName(type.getQualifiedName())
                .hidden(isHidden(type))
                .propertyDefinitions(getPropertyDescriptors(type))
                .build();
    }

    private List<ComponentPropertyDescriptor> getPropertyDescriptors(CtType<?> type) {
        return type.getFields()
                .stream()
                .filter(this::isAnnotatedProperty)
                .map(this::getPropertyDescriptor)
                .collect(Collectors.toList());
    }

    private ComponentPropertyDescriptor getPropertyDescriptor(CtField<?> field) {

        TypeDescriptor propertyType = new PrimitiveTypeDescriptor(String.class);
        boolean isRequired = isRequired(field);


        String defaultValue = (String) propertyType.defaultValue();
        if (field.hasAnnotation(Default.class)) {
            defaultValue = field.getAnnotation(Default.class).value();
        }

        String displayName = field.getAnnotation(Property.class).value();

        return new ComponentPropertyDescriptor(
                field.getSimpleName(),
                propertyType,
                displayName,
                defaultValue,
                isRequired);
    }

    private boolean isHidden(CtType<?> type) {
        return type.hasAnnotation(Hidden.class);
    }

    private boolean isESBComponent(CtType<?> type) {
        return type.hasAnnotation(ESBComponent.class);
    }

    private boolean isAnnotatedProperty(CtField<?> field) {
        return field.hasAnnotation(Property.class);
    }

    private boolean isRequired(CtField<?> field) {
        return field.hasAnnotation(Required.class);
    }
}
