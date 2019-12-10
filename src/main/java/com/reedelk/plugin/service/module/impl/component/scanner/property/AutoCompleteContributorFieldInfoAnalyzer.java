package com.reedelk.plugin.service.module.impl.component.scanner.property;

import com.reedelk.plugin.component.domain.AutoCompleteContributorDefinition;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.service.module.impl.component.scanner.ComponentAnalyzerContext;
import com.reedelk.plugin.service.module.impl.component.scanner.ScannerUtil;
import com.reedelk.runtime.api.annotation.AutoCompleteContributor;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.FieldInfo;

import java.util.List;

public class AutoCompleteContributorFieldInfoAnalyzer implements FieldInfoAnalyzer {

    @Override
    public void handle(FieldInfo propertyInfo, ComponentPropertyDescriptor.Builder builder, ComponentAnalyzerContext context) {
        if (ScannerUtil.hasAnnotation(propertyInfo, AutoCompleteContributor.class)) {
            AnnotationInfo info = propertyInfo.getAnnotationInfo(AutoCompleteContributor.class.getName());

            boolean isMessage =  ScannerUtil.booleanParameterValueFrom(info, "message", true);
            boolean isError =  ScannerUtil.booleanParameterValueFrom(info, "error", false);
            boolean isContext = ScannerUtil.booleanParameterValueFrom(info, "context", true);
            List<String> customContributions = ScannerUtil.stringListParameterValueFrom(info, "contributions");

            AutoCompleteContributorDefinition definition =
                    new AutoCompleteContributorDefinition(isMessage, isError, isContext, customContributions);
            builder.autoComplete(definition);
        }
    }
}
