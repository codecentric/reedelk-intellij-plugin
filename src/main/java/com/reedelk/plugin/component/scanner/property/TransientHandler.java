package com.reedelk.plugin.component.scanner.property;

import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.Transient;
import com.reedelk.plugin.component.scanner.ComponentAnalyzerContext;
import io.github.classgraph.FieldInfo;

public class TransientHandler implements Handler {
    @Override
    public void handle(FieldInfo propertyInfo, ComponentPropertyDescriptor.Builder builder, ComponentAnalyzerContext context) {
        Transient isTransient =
                propertyInfo.hasAnnotation(com.reedelk.runtime.api.annotation.Transient.class.getName()) ?
                        Transient.YES :
                        Transient.NO;
        builder.isTransient(isTransient);
    }
}
