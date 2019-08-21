package com.reedelk.plugin.commons;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.FieldInfo;

import static java.lang.String.format;

public class GetAnnotationValue {

    public static String getOrThrow(ClassInfo classInfo, Class<?> annotationClazz) {
        AnnotationInfo annotationInfo = classInfo.getAnnotationInfo(annotationClazz.getName());
        if (annotationInfo != null) {
            if (annotationInfo.getParameterValues() != null) {
                if (!annotationInfo.getParameterValues().isEmpty()) {
                    return (String) annotationInfo.getParameterValues().get(0).getValue();
                }
            }
        }
        throw new IllegalStateException(format("Expected mandatory annotation [%s] for class type [%s]", annotationClazz.getName(), classInfo.getName()));
    }


    public static String getOrDefault(FieldInfo fieldInfo, Class<?> annotationClazz, String defaultValue) {
        AnnotationInfo annotationInfo = fieldInfo.getAnnotationInfo(annotationClazz.getName());
        if (annotationInfo != null) {
            if (annotationInfo.getParameterValues() != null) {
                if (!annotationInfo.getParameterValues().isEmpty()) {
                    return (String) annotationInfo.getParameterValues().get(0).getValue();
                }
            }
        }
        return defaultValue;
    }
}
