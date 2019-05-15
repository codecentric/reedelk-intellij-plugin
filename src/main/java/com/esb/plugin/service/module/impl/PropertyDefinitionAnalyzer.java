package com.esb.plugin.service.module.impl;

import com.esb.api.annotation.Default;
import com.esb.api.annotation.Property;
import com.esb.api.annotation.Required;
import com.esb.plugin.component.PropertyDescriptor;
import com.esb.plugin.converter.PropertyValueConverterFactory;
import com.google.common.base.Defaults;
import io.github.classgraph.*;

import java.util.Optional;

class PropertyDefinitionAnalyzer {

    private final ComponentAnalyzerContext context;

    PropertyDefinitionAnalyzer(ComponentAnalyzerContext context) {
        this.context = context;
    }

    Optional<PropertyDescriptor> analyze(FieldInfo fieldInfo) {
        if (!isComponentProperty(fieldInfo)) return Optional.empty();

        Class<?> propertyType = getPropertyType(fieldInfo);
        Object defaultValue = getDefaultValue(fieldInfo, propertyType);
        String propertyName = getPropertyName(fieldInfo);
        String displayName = getDisplayName(fieldInfo);
        boolean required = isRequired(fieldInfo);


        PropertyDescriptor definition = new PropertyDescriptor(
                propertyName,
                displayName,
                propertyType,
                required,
                defaultValue);

        return Optional.of(definition);
    }

    private boolean isComponentProperty(FieldInfo fieldInfo) {
        return fieldInfo.hasAnnotation(Property.class.getName());
    }

    private boolean isRequired(FieldInfo fieldInfo) {
        return fieldInfo.hasAnnotation(Required.class.getName());
    }

    private String getPropertyName(FieldInfo fieldInfo) {
        return fieldInfo.getName();
    }

    private String getDisplayName(FieldInfo fieldInfo) {
        AnnotationInfo propertyAnnotationInfo = fieldInfo.getAnnotationInfo(Property.class.getName());
        if (propertyAnnotationInfo != null) {
            AnnotationParameterValueList parameterValues = propertyAnnotationInfo.getParameterValues();
            return (String) parameterValues.getValue("value");
        } else {
            return fieldInfo.getName();
        }
    }

    private Object getDefaultValue(FieldInfo fieldInfo, Class<?> expectedType) {
        boolean hasDefault = fieldInfo.hasAnnotation(Default.class.getName());
        if (hasDefault) {
            AnnotationInfo defaultAnnotationInfo = fieldInfo.getAnnotationInfo(Default.class.getName());
            AnnotationParameterValueList defaultAnnotationParameterValueList = defaultAnnotationInfo.getParameterValues();
            String value = (String) defaultAnnotationParameterValueList.getValue("value");
            return PropertyValueConverterFactory.forType(expectedType).from(value);
        }
        return Defaults.defaultValue(expectedType);
    }

    private Class<?> getPropertyType(FieldInfo fieldInfo) {
        TypeSignature typeSignature = fieldInfo.getTypeDescriptor();
        if (typeSignature instanceof BaseTypeSignature) {
            BaseTypeSignature baseType = (BaseTypeSignature) typeSignature;
            return baseType.getType();
        } else if (typeSignature instanceof ClassRefTypeSignature) {

            ClassRefTypeSignature classRefType = (ClassRefTypeSignature) typeSignature;
            String fullyQualifiedClassName = classRefType.getFullyQualifiedClassName();

            if (PropertyValueConverterFactory.isKnownType(fullyQualifiedClassName)) {
                try {
                    return Class.forName(classRefType.getFullyQualifiedClassName());
                } catch (ClassNotFoundException e) {
                    // if it is a known type, then the class must be resolvable in the classpath.
                }
            } else if (isEnum(fullyQualifiedClassName)) {
                return Enum.class;
            }
        }

        throw new IllegalStateException("Unsupported type");
    }


    private boolean isEnum(String fullyQualifiedClassName) {
        ClassInfo classInfo = context.getClassInfo(fullyQualifiedClassName);
        return classInfo
                .getSuperclasses()
                .stream()
                .anyMatch(info -> info.getName().equals(Enum.class.getName()));
    }


}
