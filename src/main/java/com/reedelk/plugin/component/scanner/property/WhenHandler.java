package com.reedelk.plugin.component.scanner.property;

import com.intellij.openapi.diagnostic.Logger;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.WhenDefinition;
import com.reedelk.plugin.component.scanner.ComponentAnalyzerContext;
import com.reedelk.runtime.api.annotation.When;
import com.reedelk.runtime.api.annotation.Whens;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationParameterValue;
import io.github.classgraph.AnnotationParameterValueList;
import io.github.classgraph.FieldInfo;

public class WhenHandler implements Handler {

    private static final Logger LOG = Logger.getInstance(WhenHandler.class);

    @Override
    public void handle(FieldInfo propertyInfo, ComponentPropertyDescriptor.Builder builder, ComponentAnalyzerContext context) {
        // Check if there is a single 'when' annotation
        boolean hasWhenAnnotation = propertyInfo.hasAnnotation(When.class.getName());
        if (hasWhenAnnotation) {
            AnnotationInfo info = propertyInfo.getAnnotationInfo(When.class.getName());
            WhenDefinition variableDefinition = processWhenInfo(info);
            builder.when(variableDefinition);
        }

        // More than one 'when' definition
        boolean hasWhenAnnotations = propertyInfo.hasAnnotation(Whens.class.getName());
        if (hasWhenAnnotations) {
            AnnotationInfo annotationInfo = propertyInfo.getAnnotationInfo(Whens.class.getName());
            AnnotationParameterValueList whensAnnotationList = annotationInfo.getParameterValues();
            Object[] annotationInfos = (Object[]) whensAnnotationList.get(0).getValue();
            for (Object info : annotationInfos) {
                try {
                    WhenDefinition whenDefinition = processWhenInfo((AnnotationInfo) info);
                    builder.when(whenDefinition);
                } catch (Exception e) {
                    // TODO: Hardcodedstring
                    LOG.warn(String.format("Could not process When annotation info for property named '%s'", propertyInfo.getName()), e);
                }
            }
        }
    }

    private WhenDefinition processWhenInfo(AnnotationInfo info) {
        String propertyName = getParameterValue(info, "propertyName");
        String propertyValue = getParameterValue(info, "propertyValue");
        return new WhenDefinition(propertyName, propertyValue);
    }

    private String getParameterValue(AnnotationInfo info, String parameterName) {
        AnnotationParameterValueList parameterValues = info.getParameterValues();
        AnnotationParameterValue parameterValue = parameterValues.get(parameterName);
        return parameterValue != null ? (String) parameterValue.getValue() : null;
    }
}
