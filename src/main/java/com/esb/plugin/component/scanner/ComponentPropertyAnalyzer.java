package com.esb.plugin.component.scanner;

import com.esb.api.annotation.Default;
import com.esb.api.annotation.Property;
import com.esb.api.annotation.Required;
import com.esb.plugin.component.domain.*;
import com.esb.plugin.converter.ValueConverterFactory;
import io.github.classgraph.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.esb.plugin.converter.ValueConverterFactory.isKnownType;

public class ComponentPropertyAnalyzer {

    private static final String ANNOTATION_DEFAULT_PARAM_NAME = "value";

    private final ComponentAnalyzerContext context;

    public ComponentPropertyAnalyzer(ComponentAnalyzerContext context) {
        this.context = context;
    }

    public Optional<ComponentPropertyDescriptor> analyze(FieldInfo fieldInfo) {
        return fieldInfo.hasAnnotation(Property.class.getName()) ?
                Optional.of(analyzeProperty(fieldInfo)) :
                Optional.empty();
    }

    private ComponentPropertyDescriptor analyzeProperty(FieldInfo propertyInfo) {
        String propertyName = propertyInfo.getName();
        String displayName = getAnnotationValueOrDefault(propertyInfo, Property.class, propertyInfo.getName());
        TypeDescriptor propertyType = getPropertyType(propertyInfo);

        Object defaultValue = getDefaultValue(propertyInfo, propertyType);

        PropertyRequired required = propertyInfo.hasAnnotation(Required.class.getName()) ?
                PropertyRequired.REQUIRED :
                PropertyRequired.NOT_REQUIRED;
        return new ComponentPropertyDescriptor(propertyName, propertyType, displayName, defaultValue, required);
    }

    private TypeDescriptor getPropertyType(FieldInfo fieldInfo) {
        TypeSignature typeSignature = fieldInfo.getTypeDescriptor();
        if (typeSignature instanceof BaseTypeSignature) {
            return processBaseType((BaseTypeSignature) typeSignature);
        } else if (typeSignature instanceof ClassRefTypeSignature) {
            return processClassRefType((ClassRefTypeSignature) typeSignature);
        } else {
            throw new UnsupportedType(typeSignature.getClass());
        }
    }

    private TypeDescriptor processBaseType(BaseTypeSignature typeSignature) {
        return new PrimitiveTypeDescriptor(typeSignature.getType());
    }

    private TypeDescriptor processClassRefType(ClassRefTypeSignature typeSignature) {
        String fullyQualifiedClassName = typeSignature.getFullyQualifiedClassName();
        if (isKnownType(fullyQualifiedClassName)) {
            try {
                return new PrimitiveTypeDescriptor(Class.forName(fullyQualifiedClassName));
            } catch (ClassNotFoundException e) {
                // if it is a known type, then the class must be resolvable.
                // Otherwise the @PropertyValueConverterFactory class would not even compile.
                throw new UnsupportedType(fullyQualifiedClassName);
            }
        } else if (isEnumeration(fullyQualifiedClassName)) {
            return processEnumType(typeSignature);
        } else {
            throw new UnsupportedType(fullyQualifiedClassName);
        }
    }

    // TODO: Test corner cases like when there is an enum with no fields!
    private EnumTypeDescriptor processEnumType(ClassRefTypeSignature enumRefType) {
        ClassInfo enumClassInfo = context.getClassInfo(enumRefType.getFullyQualifiedClassName());
        FieldInfoList declaredFieldInfo = enumClassInfo.getDeclaredFieldInfo();
        List<String> enumNames = declaredFieldInfo
                .stream()
                .filter(fieldInfo -> fieldInfo.getTypeDescriptor() instanceof ClassRefTypeSignature)
                .map(FieldInfo::getName)
                .collect(Collectors.toList());
        return new EnumTypeDescriptor(enumNames, enumNames.get(0));
    }

    private boolean isEnumeration(String fullyQualifiedClassName) {
        ClassInfo classInfo = context.getClassInfo(fullyQualifiedClassName);
        return classInfo
                .getSuperclasses()
                .stream()
                .anyMatch(info -> info.getName().equals(Enum.class.getName()));
    }

    private Object getDefaultValue(FieldInfo propertyInfo, TypeDescriptor propertyType) {
        String stringValue = getAnnotationValueOrDefault(propertyInfo, Default.class, Default.USE_DEFAULT_VALUE);
        return Default.USE_DEFAULT_VALUE.equals(stringValue) ?
                propertyType.defaultValue() :
                ValueConverterFactory.forType(propertyType).from(stringValue);
    }

    @SuppressWarnings("unchecked")
    private <T> T getAnnotationValueOrDefault(FieldInfo fieldInfo, Class<?> annotationClazz, T defaultValue) {
        if (!fieldInfo.hasAnnotation(annotationClazz.getName())) {
            return defaultValue;
        }
        AnnotationInfo annotationInfo = fieldInfo.getAnnotationInfo(annotationClazz.getName());
        AnnotationParameterValueList parameterValues = annotationInfo.getParameterValues();
        return (T) parameterValues.getValue(ANNOTATION_DEFAULT_PARAM_NAME);
    }

}
