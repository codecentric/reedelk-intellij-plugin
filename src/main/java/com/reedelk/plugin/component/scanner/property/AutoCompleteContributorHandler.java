package com.reedelk.plugin.component.scanner.property;

import com.reedelk.plugin.component.domain.AutoCompleteContributorDefinition;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.scanner.ComponentAnalyzerContext;
import com.reedelk.runtime.api.annotation.AutoCompleteContributor;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationParameterValue;
import io.github.classgraph.AnnotationParameterValueList;
import io.github.classgraph.FieldInfo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AutoCompleteContributorHandler implements Handler {

    @Override
    public void handle(FieldInfo propertyInfo, ComponentPropertyDescriptor.Builder builder, ComponentAnalyzerContext context) {
        // Check if there is an 'AutoCompleteContributor' annotation
        boolean hasAutoCompleteContributorAnnotation = propertyInfo.hasAnnotation(AutoCompleteContributor.class.getName());
        if (hasAutoCompleteContributorAnnotation) {
            AnnotationInfo info = propertyInfo.getAnnotationInfo(AutoCompleteContributor.class.getName());
            boolean hasMessage = getBooleanParameterValue(info, "message");
            boolean hasContext = getBooleanParameterValue(info, "context");
            List<String> contributions = getStringListParameterValue(info, "contributions");
            AutoCompleteContributorDefinition definition =
                    new AutoCompleteContributorDefinition(hasMessage, hasContext, contributions);
            builder.autoCompleteContributor(definition);
        }
    }

    private boolean getBooleanParameterValue(AnnotationInfo info, String parameterName) {
        AnnotationParameterValueList parameterValues = info.getParameterValues();
        AnnotationParameterValue parameterValue = parameterValues.get(parameterName);
        return parameterValue != null && (boolean) parameterValue.getValue();
    }

    private List<String> getStringListParameterValue(AnnotationInfo info, String parameterName) {
        AnnotationParameterValueList parameterValues = info.getParameterValues();
        AnnotationParameterValue parameterValue = parameterValues.get(parameterName);
        Object[] array = (Object[]) parameterValue.getValue();
        return Arrays.stream(array).map(o -> (String) o).collect(Collectors.toList());
    }
}
