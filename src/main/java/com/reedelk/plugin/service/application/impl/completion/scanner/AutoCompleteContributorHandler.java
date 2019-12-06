package com.reedelk.plugin.service.application.impl.completion.scanner;

public class AutoCompleteContributorHandler {

    /**
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
    }*/
}
