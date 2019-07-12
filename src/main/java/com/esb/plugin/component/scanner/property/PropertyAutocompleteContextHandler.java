package com.esb.plugin.component.scanner.property;

import com.esb.api.annotation.AutocompleteContexts;
import com.esb.api.annotation.AutocompleteType;
import com.esb.plugin.component.domain.AutocompleteContext;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.scanner.ComponentAnalyzerContext;
import com.intellij.openapi.diagnostic.Logger;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationParameterValue;
import io.github.classgraph.AnnotationParameterValueList;
import io.github.classgraph.FieldInfo;

public class PropertyAutocompleteContextHandler implements Handler {

    private static final Logger LOG = Logger.getInstance(PropertyAutocompleteContextHandler.class);

    @Override
    public void handle(FieldInfo propertyInfo, ComponentPropertyDescriptor.Builder builder, ComponentAnalyzerContext context) {
        boolean hasAutocompleteContextAnnotation = propertyInfo.hasAnnotation(AutocompleteContexts.class.getName());
        if (hasAutocompleteContextAnnotation) {

            AnnotationInfo annotationInfo = propertyInfo.getAnnotationInfo(AutocompleteContexts.class.getName());
            AnnotationParameterValueList autocompleteContextsList = annotationInfo.getParameterValues();
            Object[] annotationInfos = (Object[]) autocompleteContextsList.get(0).getValue();

            for (Object info : annotationInfos) {
                try {
                    AutocompleteContext autocompleteContext = processAutocompleteContextInfo((AnnotationInfo) info, propertyInfo.getName());
                    builder.autocompleteContext(autocompleteContext);
                } catch (Exception e) {
                    LOG.warn(String.format("Could not process AutocompleteContext info for property named '%s'", propertyInfo.getName()));
                }
            }
        }
    }

    private AutocompleteContext processAutocompleteContextInfo(AnnotationInfo info, String propertyName) {
        String contextName = (String) getParameterValue(info, "name");
        String file = (String) getParameterValue(info, "file");
        AutocompleteType type = (AutocompleteType) getParameterValue(info, "type");
        return new AutocompleteContext(contextName, type, propertyName, file);
    }

    private Object getParameterValue(AnnotationInfo info, String parameterName) {
        AnnotationParameterValueList parameterValues = info.getParameterValues();
        AnnotationParameterValue parameterValue = parameterValues.get(parameterName);
        return parameterValue.getValue();
    }
}
