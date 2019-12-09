package com.reedelk.plugin.service.module.impl.commons;

import com.reedelk.plugin.component.domain.Collapsible;
import com.reedelk.plugin.component.domain.Shared;
import com.reedelk.plugin.service.module.impl.component.scanner.ComponentAnalyzerContext;
import com.reedelk.plugin.service.module.impl.component.scanner.UnsupportedType;
import com.reedelk.runtime.api.annotation.*;
import com.reedelk.runtime.api.script.Script;
import com.reedelk.runtime.api.script.dynamicmap.DynamicMap;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicValue;
import io.github.classgraph.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableList;

public class ScannerUtil {

    private static final String ANNOTATION_DEFAULT_PARAM_NAME = "value";

    private ScannerUtil() {
    }

    public static boolean hasAnnotation(FieldInfo fieldInfo, Class<?> clazz) {
        return fieldInfo.hasAnnotation(clazz.getName());
    }

    public static boolean hasNotAnnotation(FieldInfo fieldInfo, Class<?> clazz) {
        return !hasAnnotation(fieldInfo, clazz);
    }

    public static boolean isVisibleProperty(FieldInfo fieldInfo) {
        return hasAnnotation(fieldInfo, Property.class) &&
                hasNotAnnotation(fieldInfo, Hidden.class);
    }

    public static String stringParameterValueFrom(AnnotationInfo info, String parameterName) {
        AnnotationParameterValueList parameterValues = info.getParameterValues();
        AnnotationParameterValue parameterValue = parameterValues.get(parameterName);
        return parameterValue != null ? (String) parameterValue.getValue() : null;
    }

    public static List<String> stringListParameterValueFrom(AnnotationInfo info, String parameterName) {
        AnnotationParameterValueList parameterValues = info.getParameterValues();
        AnnotationParameterValue parameterValue = parameterValues.get(parameterName);
        if (parameterValue == null) return unmodifiableList(new ArrayList<>());
        Object[] array = (Object[]) parameterValue.getValue();
        return array == null ?
                unmodifiableList(new ArrayList<>()) :
                stream(array).map(o -> (String) o).collect(Collectors.toList());
    }

    public static boolean booleanParameterValueFrom(AnnotationInfo info, String parameterName, boolean defaultValue) {
        AnnotationParameterValueList parameterValues = info.getParameterValues();
        if (parameterValues == null) return defaultValue;
        AnnotationParameterValue parameterValue = parameterValues.get(parameterName);
        return parameterValue == null ? defaultValue : (boolean) parameterValue.getValue();
    }

    @SuppressWarnings("unchecked")
    public static <T> T annotationValueOrDefaultFrom(FieldInfo fieldInfo, Class<?> annotationClazz, T defaultValue) {
        if (!fieldInfo.hasAnnotation(annotationClazz.getName())) {
            return defaultValue;
        }
        AnnotationInfo annotationInfo = fieldInfo.getAnnotationInfo(annotationClazz.getName());
        AnnotationParameterValueList parameterValues = annotationInfo.getParameterValues();
        if (parameterValues == null) return defaultValue;
        return parameterValues.get(ANNOTATION_DEFAULT_PARAM_NAME) == null ?
                defaultValue :
                (T) parameterValues.getValue(ANNOTATION_DEFAULT_PARAM_NAME);
    }

    @SuppressWarnings("unchecked")
    public static <T> T annotationParameterValueOrDefaultFrom(FieldInfo fieldInfo, Class<?> annotationClazz, String annotationParamName, T defaultValue) {
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

    /**
     * Returns a new Predicate which filters FieldInfo's having type the target
     * class name specified in the argument of this function.
     */
    public static Predicate<FieldInfo> filterByFullyQualifiedClassNameType(String targetFullyQualifiedClassName) {
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
    public static boolean isEnumeration(String fullyQualifiedClassName, ComponentAnalyzerContext context) {
        ClassInfo classInfo = context.getClassInfo(fullyQualifiedClassName);
        if (classInfo == null) return false;

        ClassInfoList superclasses = classInfo.getSuperclasses();
        if (superclasses == null) return false;

        return superclasses
                .stream()
                .anyMatch(info -> info.getName().equals(Enum.class.getName()));
    }

    public static boolean isDynamicValue(Class<?> clazz) {
        try {
            return DynamicValue.class.equals(clazz.getSuperclass());
        } catch (UnsupportedType exception) {
            return false;
        }
    }

    public static boolean isDynamicMap(Class<?> clazz) {
        try {
            return DynamicMap.class.equals(clazz.getSuperclass());
        } catch (UnsupportedType exception) {
            return false;
        }
    }

    public static Class<?> clazzByFullyQualifiedName(String fullyQualifiedClassName) {
        try {
            return Class.forName(fullyQualifiedClassName);
        } catch (ClassNotFoundException e) {
            // if it is a known type, then the class must be resolvable.
            // Otherwise the @PropertyValueConverterFactory class would not even compile.
            throw new UnsupportedType(fullyQualifiedClassName);
        }
    }

    public static boolean isFile(FieldInfo fieldInfo, Class<?> clazz) {
        return hasAnnotation(fieldInfo, File.class) && String.class.equals(clazz);
    }

    public static boolean isMimeTypeCombo(FieldInfo fieldInfo, Class<?> clazz) {
        return hasAnnotation(fieldInfo, MimeTypeCombo.class) && String.class.equals(clazz);
    }

    public static boolean isCombo(FieldInfo fieldInfo, Class<?> clazz) {
        return fieldInfo.hasAnnotation(Combo.class.getName()) && String.class.equals(clazz);
    }

    public static boolean isPassword(FieldInfo fieldInfo, Class<?> clazz) {
        return hasAnnotation(fieldInfo, Password.class) && String.class.equals(clazz);
    }

    public static boolean isMap(Class<?> clazz) {
        return Map.class.equals(clazz);
    }

    public static boolean isScript(Class<?> clazz) {
        return Script.class.equals(clazz);
    }

    public static Shared isShareable(ClassInfo classInfo) {
        return classInfo.hasAnnotation(com.reedelk.runtime.api.annotation.Shared.class.getName()) ?
                Shared.YES : Shared.NO;
    }

    public static Collapsible isCollapsible(ClassInfo classInfo) {
        return classInfo.hasAnnotation(com.reedelk.runtime.api.annotation.Collapsible.class.getName()) ?
                Collapsible.YES : Collapsible.NO;
    }

    private static Object getParameterValue(AnnotationInfo info, String parameterName) {
        AnnotationParameterValueList parameterValues = info.getParameterValues();
        AnnotationParameterValue parameterValue = parameterValues.get(parameterName);
        return parameterValue == null ? parameterValue : parameterValue.getValue();
    }

    public static boolean isHidden(ClassInfo classInfo) {
        return classInfo.hasAnnotation(Hidden.class.getName());
    }
}
