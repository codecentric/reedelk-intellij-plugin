package com.reedelk.plugin.service.module.impl.component.scanner.property;

import com.intellij.openapi.diagnostic.Logger;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.WhenDefinition;
import com.reedelk.plugin.message.ReedelkBundle;
import com.reedelk.plugin.service.module.impl.commons.ScannerUtil;
import com.reedelk.plugin.service.module.impl.component.scanner.ComponentAnalyzerContext;
import com.reedelk.runtime.api.annotation.When;
import com.reedelk.runtime.api.annotation.Whens;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationParameterValueList;
import io.github.classgraph.FieldInfo;

public class WhenFieldInfoAnalyzer implements FieldInfoAnalyzer {

    private static final Logger LOG = Logger.getInstance(WhenFieldInfoAnalyzer.class);

    @Override
    public void handle(FieldInfo propertyInfo, ComponentPropertyDescriptor.Builder builder, ComponentAnalyzerContext context) {
        // Check if there is a single 'when' annotation
        boolean hasWhenAnnotation = ScannerUtil.hasAnnotation(propertyInfo, When.class);
        if (hasWhenAnnotation) {
            AnnotationInfo info = propertyInfo.getAnnotationInfo(When.class.getName());
            WhenDefinition variableDefinition = processWhenInfo(info);
            builder.when(variableDefinition);
        }

        // More than one 'when' definition
        boolean hasWhenAnnotations = ScannerUtil.hasAnnotation(propertyInfo, Whens.class);
        if (hasWhenAnnotations) {
            AnnotationInfo annotationInfo = propertyInfo.getAnnotationInfo(Whens.class.getName());
            AnnotationParameterValueList whensAnnotationList = annotationInfo.getParameterValues();
            Object[] annotationInfos = (Object[]) whensAnnotationList.get(0).getValue();
            for (Object info : annotationInfos) {
                try {
                    WhenDefinition whenDefinition = processWhenInfo((AnnotationInfo) info);
                    builder.when(whenDefinition);
                } catch (Exception exception) {
                    String message = ReedelkBundle.message("component.scanner.error.when.annotation", propertyInfo.getName());
                    LOG.warn(message, exception);
                }
            }
        }
    }

    private WhenDefinition processWhenInfo(AnnotationInfo info) {
        String propertyName = ScannerUtil.stringParameterValueFrom(info, "propertyName");
        String propertyValue = ScannerUtil.stringParameterValueFrom(info, "propertyValue");
        return new WhenDefinition(propertyName, propertyValue);
    }
}
