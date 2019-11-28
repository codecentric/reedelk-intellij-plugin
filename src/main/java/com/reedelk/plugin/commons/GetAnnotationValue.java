package com.reedelk.plugin.commons;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.FieldInfo;

public class GetAnnotationValue {

    private GetAnnotationValue() {
    }

    public static String getOrDefault(FieldInfo fieldInfo, Class<?> annotationClazz, String defaultValue) {
        AnnotationInfo annotationInfo = fieldInfo.getAnnotationInfo(annotationClazz.getName());
        if (areParameterValuesNotNull(annotationInfo)) {
            return (String) annotationInfo.getParameterValues().get(0).getValue();
        }
        return defaultValue;
    }

    private static boolean areParameterValuesNotNull(AnnotationInfo annotationInfo) {
        return annotationInfo != null &&
                annotationInfo.getParameterValues() != null &&
                !annotationInfo.getParameterValues().isEmpty();
    }
}
