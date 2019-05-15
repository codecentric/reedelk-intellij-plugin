package com.esb.plugin.service.module.impl;

import com.esb.api.annotation.Default;
import com.esb.api.annotation.Property;
import com.esb.api.annotation.Required;
import com.esb.plugin.component.ComponentPropertyDescriptor;
import com.esb.plugin.component.EnumTypeDescriptor;
import com.esb.plugin.component.PrimitiveTypeDescriptor;
import com.esb.plugin.component.PropertyTypeDescriptor;
import io.github.classgraph.*;

import java.util.Arrays;
import java.util.Optional;

import static com.esb.plugin.converter.PropertyValueConverterFactory.isKnownType;

class PropertyDefinitionAnalyzer {

    private final ComponentAnalyzerContext context;

    PropertyDefinitionAnalyzer(ComponentAnalyzerContext context) {
        this.context = context;
    }

    Optional<ComponentPropertyDescriptor> analyze(FieldInfo fieldInfo) {
        return fieldInfo.hasAnnotation(Property.class.getName()) ?
                Optional.of(analyzeProperty(fieldInfo)) :
                Optional.empty();
    }

    private ComponentPropertyDescriptor analyzeProperty(FieldInfo propertyInfo) {
        String propertyName = propertyInfo.getName();
        String displayName = getAnnotationValueOrDefault(propertyInfo, Property.class, propertyInfo.getName());
        PropertyTypeDescriptor propertyType = getPropertyType(propertyInfo);
        Object defaultValue = getAnnotationValueOrDefault(propertyInfo, Default.class, propertyType.defaultValue());
        boolean required = propertyInfo.hasAnnotation(Required.class.getName());
        return new ComponentPropertyDescriptor(propertyName, displayName, required, defaultValue, propertyType);
    }


    private PropertyTypeDescriptor getPropertyType(FieldInfo fieldInfo) {
        TypeSignature typeSignature = fieldInfo.getTypeDescriptor();
        if (typeSignature instanceof BaseTypeSignature) {
            return processBaseType((BaseTypeSignature) typeSignature);
        } else if (typeSignature instanceof ClassRefTypeSignature) {
            return processClassRefType((ClassRefTypeSignature) typeSignature);
        } else {
            throw new UnsupportedType(typeSignature.getClass());
        }
    }

    private PropertyTypeDescriptor processBaseType(BaseTypeSignature typeSignature) {
        return new PrimitiveTypeDescriptor(typeSignature.getType());
    }

    private PropertyTypeDescriptor processClassRefType(ClassRefTypeSignature typeSignature) {
        String fullyQualifiedClassName = typeSignature.getFullyQualifiedClassName();
        if (isKnownType(fullyQualifiedClassName)) {
            try {
                return new PrimitiveTypeDescriptor(Class.forName(fullyQualifiedClassName));
            } catch (ClassNotFoundException e) {
                // if it is a known type, then the class must be resolvable.
                // Otherwise the @PropertyValueConverterFactory class would not even compile.
                throw new UnsupportedType(fullyQualifiedClassName);
            }
        } else if (isEnum(fullyQualifiedClassName)) {
            return processEnumType(typeSignature);
        } else {
            throw new UnsupportedType(fullyQualifiedClassName);
        }
    }

    private EnumTypeDescriptor processEnumType(ClassRefTypeSignature enumRefType) {
        // TODO: Finish me!
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
        if (!fieldInfo.hasAnnotation(annotationClazz.getName())) {
            return defaultValue;
        }
        AnnotationInfo annotationInfo = fieldInfo.getAnnotationInfo(annotationClazz.getName());
        AnnotationParameterValueList parameterValues = annotationInfo.getParameterValues();
        return (T) parameterValues.getValue("value");
    }

}
