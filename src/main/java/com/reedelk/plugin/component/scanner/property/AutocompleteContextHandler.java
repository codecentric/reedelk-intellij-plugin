package com.reedelk.plugin.component.scanner.property;

import com.intellij.openapi.diagnostic.Logger;
import com.reedelk.plugin.component.domain.AutocompleteContext;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.scanner.ComponentAnalyzerContext;
import com.reedelk.runtime.api.annotation.AutocompleteContexts;
import com.reedelk.runtime.api.annotation.AutocompleteType;
import io.github.classgraph.*;

public class AutocompleteContextHandler implements Handler {

    private static final Logger LOG = Logger.getInstance(AutocompleteContextHandler.class);

    @Override
    public void handle(FieldInfo propertyInfo, ComponentPropertyDescriptor.Builder builder, ComponentAnalyzerContext context) {
        // Check if there is a single annotation
        boolean hasAutocompleteContextAnnotation = propertyInfo.hasAnnotation(com.reedelk.runtime.api.annotation.AutocompleteContext.class.getName());
        if (hasAutocompleteContextAnnotation) {
            AnnotationInfo info = propertyInfo.getAnnotationInfo(com.reedelk.runtime.api.annotation.AutocompleteContext.class.getName());
            AutocompleteContext autocompleteContext = processAutocompleteContextInfo(info, propertyInfo.getName());
            builder.context(autocompleteContext);
        }

        // If there are more than one
        boolean hasAutocompleteContextsAnnotation = propertyInfo.hasAnnotation(AutocompleteContexts.class.getName());
        if (hasAutocompleteContextsAnnotation) {

            AnnotationInfo annotationInfo = propertyInfo.getAnnotationInfo(AutocompleteContexts.class.getName());
            AnnotationParameterValueList autocompleteContextsList = annotationInfo.getParameterValues();
            Object[] annotationInfos = (Object[]) autocompleteContextsList.get(0).getValue();

            for (Object info : annotationInfos) {
                try {
                    AutocompleteContext autocompleteContext = processAutocompleteContextInfo((AnnotationInfo) info, propertyInfo.getName());
                    builder.context(autocompleteContext);
                } catch (Exception e) {
                    LOG.warn(String.format("Could not process AutocompleteContext info for property named '%s'", propertyInfo.getName()), e);
                }
            }
        }
    }

    private AutocompleteContext processAutocompleteContextInfo(AnnotationInfo info, String propertyName) {
        String contextName = (String) getParameterValue(info, "name");
        String file = (String) getParameterValue(info, "file");
        Object autocompleteType = getParameterValue(info, "type");
        if (autocompleteType == null) {
            throw new IllegalStateException(String.format("Autocomplete context type for property name=%s must not be null", propertyName));
        }
        AnnotationEnumValue type = (AnnotationEnumValue) autocompleteType;
        return new AutocompleteContext(contextName, AutocompleteType.valueOf(type.getValueName()), propertyName, file);
    }

    private Object getParameterValue(AnnotationInfo info, String parameterName) {
        AnnotationParameterValueList parameterValues = info.getParameterValues();
        AnnotationParameterValue parameterValue = parameterValues.get(parameterName);
        return parameterValue == null ? parameterValue : parameterValue.getValue();
    }
}