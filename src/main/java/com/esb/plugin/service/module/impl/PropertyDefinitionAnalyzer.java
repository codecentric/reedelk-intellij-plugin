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
        if (!hasPropertyAnnotation(fieldInfo)) return Optional.empty();

        boolean required = isRequired(fieldInfo);
        String displayName = getDisplayName(fieldInfo);
        String propertyName = getPropertyName(fieldInfo);
        PropertyTypeDescriptor propertyType = getPropertyType(fieldInfo);
        Object defaultValue = getDefaultValue(fieldInfo, propertyType);

        ComponentPropertyDescriptor definition =
                new ComponentPropertyDescriptor(propertyName, displayName, required, defaultValue, propertyType);

        return Optional.of(definition);
    }

    private boolean hasPropertyAnnotation(FieldInfo fieldInfo) {
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

    private Object getDefaultValue(FieldInfo fieldInfo, PropertyTypeDescriptor expectedType) {
        boolean hasDefault = fieldInfo.hasAnnotation(Default.class.getName());
        if (hasDefault) {
            AnnotationInfo defaultAnnotationInfo = fieldInfo.getAnnotationInfo(Default.class.getName());
            AnnotationParameterValueList defaultAnnotationParameterValueList = defaultAnnotationInfo.getParameterValues();
            String value = (String) defaultAnnotationParameterValueList.getValue("value");
            return PropertyValueConverterFactory.forType(expectedType).from(value);
        }
        return expectedType.defaultValue();
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

}
