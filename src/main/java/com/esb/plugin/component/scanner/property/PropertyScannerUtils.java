package com.esb.plugin.component.scanner.property;

import com.esb.api.annotation.File;
import com.esb.api.annotation.Required;
import com.esb.api.annotation.Script;
import com.esb.api.annotation.Shareable;
import com.esb.plugin.component.scanner.ComponentAnalyzerContext;
import io.github.classgraph.*;

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
        return classInfo
                .getSuperclasses()
                .stream()
                .anyMatch(info -> info.getName().equals(Enum.class.getName()));
    }

    // A property is a Script if and only if it has
    // @Script annotation AND type String
    static boolean isScript(FieldInfo fieldInfo, Class<?> clazz) {
        return fieldInfo.hasAnnotation(Script.class.getName()) &&
                String.class.equals(clazz);
    }

    // A property is a File if and only if it has
    // @File annotation AND type String
    static boolean isFile(FieldInfo fieldInfo, Class<?> clazz) {
        return fieldInfo.hasAnnotation(File.class.getName()) &&
                String.class.equals(clazz);
    }

    static boolean isShareable(ClassInfo classInfo) {
        return classInfo.hasAnnotation(Shareable.class.getName());
    }

    static boolean isRequired(FieldInfo fieldInfo) {
        return fieldInfo.hasAnnotation(Required.class.getName());
    }
}
