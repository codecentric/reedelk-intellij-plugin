package com.reedelk.plugin.commons;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.FieldInfo;

public class GetAnnotationValue {

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
