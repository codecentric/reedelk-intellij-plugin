package com.esb.plugin.service.module.impl;

import com.esb.api.annotation.Default;
import com.esb.api.annotation.Property;
import com.esb.api.annotation.Required;
import com.esb.plugin.component.ComponentPropertyDescriptor;
import com.esb.plugin.component.EnumTypeDescriptor;
import com.esb.plugin.component.PrimitiveTypeDescriptor;
import com.esb.plugin.component.PropertyTypeDescriptor;
import com.esb.plugin.converter.PropertyValueConverterFactory;
import io.github.classgraph.*;

import java.util.Arrays;
import java.util.Optional;

class PropertyDefinitionAnalyzer {

    private final ComponentAnalyzerContext context;

    PropertyDefinitionAnalyzer(ComponentAnalyzerContext context) {
        this.context = context;
    }

    Optional<ComponentPropertyDescriptor> analyze(FieldInfo fieldInfo) {
        if (fieldInfo.hasAnnotation(Property.class.getName())) {
            String propertyName = fieldInfo.getName();
            boolean required = fieldInfo.hasAnnotation(Required.class.getName());
            String displayName = getAnnotationValueOrDefault(fieldInfo, Property.class, fieldInfo.getName());

            PropertyTypeDescriptor propertyType = getPropertyType(fieldInfo);
            Object defaultValue = getAnnotationValueOrDefault(fieldInfo, Default.class, propertyType.defaultValue());

            ComponentPropertyDescriptor definition =
                    new ComponentPropertyDescriptor(propertyName, displayName, required, defaultValue, propertyType);

            return Optional.of(definition);

        } else {
            return Optional.empty();
        }

    }

    private PropertyTypeDescriptor getPropertyType(FieldInfo fieldInfo) {
        TypeSignature typeSignature = fieldInfo.getTypeDescriptor();
        if (typeSignature instanceof BaseTypeSignature) {
            return processBaseType((BaseTypeSignature) typeSignature);
        } else if (typeSignature instanceof ClassRefTypeSignature) {
            return processClassRefType((ClassRefTypeSignature) typeSignature);
        } else {
            throw new IllegalStateException("Unsupported type");
        }
    }

    private PropertyTypeDescriptor processBaseType(BaseTypeSignature typeSignature) {
        return new PrimitiveTypeDescriptor(typeSignature.getType());
    }

    private PropertyTypeDescriptor processClassRefType(ClassRefTypeSignature typeSignature) {
        String fullyQualifiedClassName = typeSignature.getFullyQualifiedClassName();
        if (PropertyValueConverterFactory.isKnownType(fullyQualifiedClassName)) {
            try {
                return new PrimitiveTypeDescriptor(Class.forName(typeSignature.getFullyQualifiedClassName()));
            } catch (ClassNotFoundException e) {
                // if it is a known type, then the class must be resolvable.
                // Otherwise the @PropertyValueConverterFactory class would not even
                // compile.
                throw new IllegalStateException("Unsupported Type");
            }
        } else if (isEnum(fullyQualifiedClassName)) {
            return processEnumType(typeSignature);
        } else {
            throw new IllegalStateException("Unsupported Type");
        }
    }

    private EnumTypeDescriptor processEnumType(ClassRefTypeSignature enumRefType) {
        return new EnumTypeDescriptor(Arrays.asList("one", "two"), "one");
    }

    private boolean isEnum(String fullyQualifiedClassName) {
        ClassInfo classInfo = context.getClassInfo(fullyQualifiedClassName);
        return classInfo
                .getSuperclasses()
                .stream()
                .anyMatch(info -> info.getName().equals(Enum.class.getName()));
    }

    @SuppressWarnings("unchecked")
    private <T> T getAnnotationValueOrDefault(FieldInfo fieldInfo, Class<?> annotationClazz, T defaultValue) {
        if (fieldInfo.hasAnnotation(annotationClazz.getName())) {
            AnnotationInfo annotationInfo = fieldInfo.getAnnotationInfo(annotationClazz.getName());
            AnnotationParameterValueList parameterValues = annotationInfo.getParameterValues();
            return (T) parameterValues.getValue("value");
        } else {
            return defaultValue;
        }
    }

}
