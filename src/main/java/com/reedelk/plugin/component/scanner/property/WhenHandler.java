package com.reedelk.plugin.component.scanner.property;

import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.WhenDefinition;
import com.reedelk.plugin.component.scanner.ComponentAnalyzerContext;
import com.reedelk.runtime.api.annotation.When;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationParameterValue;
import io.github.classgraph.AnnotationParameterValueList;
import io.github.classgraph.FieldInfo;

public class WhenHandler implements Handler {

    @Override
    public void handle(FieldInfo propertyInfo, ComponentPropertyDescriptor.Builder builder, ComponentAnalyzerContext context) {
        // Check if there is a single 'when' annotation
        boolean hasWhenAnnotation = propertyInfo.hasAnnotation(When.class.getName());
        if (hasWhenAnnotation) {
            AnnotationInfo info = propertyInfo.getAnnotationInfo(When.class.getName());
            WhenDefinition variableDefinition = processWhenInfo(info);
            builder.when(variableDefinition);
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
