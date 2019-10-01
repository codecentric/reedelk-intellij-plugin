package com.reedelk.plugin.component.scanner.property;

import com.reedelk.plugin.component.domain.Collapsible;
import com.reedelk.plugin.component.domain.Shared;
import com.reedelk.plugin.component.scanner.ComponentAnalyzerContext;
import com.reedelk.plugin.component.scanner.UnsupportedType;
import com.reedelk.runtime.api.annotation.Combo;
import com.reedelk.runtime.api.annotation.File;
import com.reedelk.runtime.api.script.Script;
import com.reedelk.runtime.api.script.dynamicmap.DynamicMap;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicValue;
import io.github.classgraph.*;

import java.util.Map;
import java.util.function.Predicate;

class PropertyScannerUtils {

    private static final String ANNOTATION_DEFAULT_PARAM_NAME = "value";

    @SuppressWarnings("unchecked")
    static <T> T getAnnotationValueOrDefault(FieldInfo fieldInfo, Class<?> annotationClazz, T defaultValue) {
        if (!fieldInfo.hasAnnotation(annotationClazz.getName())) {
            return defaultValue;
        }
        AnnotationInfo annotationInfo = fieldInfo.getAnnotationInfo(annotationClazz.getName());
        AnnotationParameterValueList parameterValues = annotationInfo.getParameterValues();
        return parameterValues.get(ANNOTATION_DEFAULT_PARAM_NAME) == null ?
                defaultValue :
                (T) parameterValues.getValue(ANNOTATION_DEFAULT_PARAM_NAME);
    }

    @SuppressWarnings("unchecked")
    static <T> T getAnnotationParameterValueOrDefault(FieldInfo fieldInfo, Class<?> annotationClazz, String annotationParamName, T defaultValue) {
        if (!fieldInfo.hasAnnotation(annotationClazz.getName())) {
            return defaultValue;
        }
        AnnotationInfo annotationInfo = fieldInfo.getAnnotationInfo(annotationClazz.getName());
        Object parameterValue = getParameterValue(annotationInfo, annotationParamName);
        if (parameterValue instanceof AnnotationEnumValue) {
            return (T) ((AnnotationEnumValue) parameterValue).loadClassAndReturnEnumValue();
        }
        return parameterValue == null ? defaultValue : (T) parameterValue;
    }

    private static Object getParameterValue(AnnotationInfo info, String parameterName) {
        AnnotationParameterValueList parameterValues = info.getParameterValues();
        AnnotationParameterValue parameterValue = parameterValues.get(parameterName);
        return parameterValue == null ? parameterValue : parameterValue.getValue();
    }

    /**
     * Returns a new Predicate which filters FieldInfo's having type the target
     * class name specified in the argument of this function.
     */
    static Predicate<FieldInfo> filterByFullyQualifiedClassNameType(String targetFullyQualifiedClassName) {
        return fieldInfo -> {
            TypeSignature typeDescriptor = fieldInfo.getTypeDescriptor();
            if (typeDescriptor instanceof ClassRefTypeSignature) {
                ClassRefTypeSignature matchingClass = (ClassRefTypeSignature) typeDescriptor;
                return matchingClass.getFullyQualifiedClassName()
                        .equals(targetFullyQualifiedClassName);
            }
            return false;
        };
    }

    /**
     * Returns true  if the given class info is of type enumeration, false otherwise.
     */
    static boolean isEnumeration(String fullyQualifiedClassName, ComponentAnalyzerContext context) {
        ClassInfo classInfo = context.getClassInfo(fullyQualifiedClassName);
        if (classInfo == null) return false;

        ClassInfoList superclasses = classInfo.getSuperclasses();
        if (superclasses == null) return false;

        return superclasses
                .stream()
                .anyMatch(info -> info.getName().equals(Enum.class.getName()));
    }

    static boolean isDynamicValue(String fullyQualifiedName) {
        try {
            return DynamicValue.class.equals(clazzByFullyQualifiedName(fullyQualifiedName).getSuperclass());
        } catch (UnsupportedType exception) {
            return false;
        }
    }

    static boolean isDynamicMap(String fullyQualifiedName) {
        try {
            return DynamicMap.class.equals(clazzByFullyQualifiedName(fullyQualifiedName).getSuperclass());
        } catch (UnsupportedType exception) {
            return false;
        }
    }

    static Class<?> clazzByFullyQualifiedName(String fullyQualifiedClassName) {
        try {
            return Class.forName(fullyQualifiedClassName);
        } catch (ClassNotFoundException e) {
            // if it is a known type, then the class must be resolvable.
            // Otherwise the @PropertyValueConverterFactory class would not even compile.
            throw new UnsupportedType(fullyQualifiedClassName);
        }
    }

    static boolean isFile(FieldInfo fieldInfo, Class<?> clazz) {
        return fieldInfo.hasAnnotation(File.class.getName()) &&
                String.class.equals(clazz);
    }

    static boolean isCombo(FieldInfo fieldInfo, Class<?> clazz) {
        return fieldInfo.hasAnnotation(Combo.class.getName()) &&
                String.class.equals(clazz);
    }

    static boolean isMap(Class<?> clazz) {
        return Map.class.equals(clazz);
    }

    static boolean isScript(Class<?> clazz) {
        return Script.class.equals(clazz);
    }

    static Shared isShareable(ClassInfo classInfo) {
        return classInfo.hasAnnotation(com.reedelk.runtime.api.annotation.Shared.class.getName()) ?
                Shared.YES : Shared.NO;
    }

    static Collapsible isCollapsible(ClassInfo classInfo) {
        return classInfo.hasAnnotation(com.reedelk.runtime.api.annotation.Collapsible.class.getName()) ?
                Collapsible.YES : Collapsible.NO;
    }

}
