package com.esb.plugin.service.module.impl;

import com.esb.api.annotation.Default;
import com.esb.api.annotation.Property;
import com.esb.api.annotation.Required;
import com.esb.plugin.converter.PropertyValueConverterFactory;
import com.google.common.base.Defaults;
import io.github.classgraph.*;

import java.util.Optional;

public class PropertyDefinitionAnalyzer {

    public Optional<PropertyDefinition> analyze(FieldInfo fieldInfo) {
        if (!isComponentProperty(fieldInfo)) return Optional.empty();

        Class<?> propertyType = getPropertyType(fieldInfo);
        Object defaultValue = getDefaultValue(fieldInfo, propertyType);
        String propertyName = getPropertyName(fieldInfo);
        String displayName = getDisplayName(fieldInfo);
        boolean required = isRequired(fieldInfo);


        PropertyDefinition definition = new PropertyDefinition(
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
            try {
                return Class.forName(classRefType.getFullyQualifiedClassName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new IllegalStateException("Unsupported type", e);
            }
        }

        throw new IllegalStateException("Unsupported type");
    }


}
