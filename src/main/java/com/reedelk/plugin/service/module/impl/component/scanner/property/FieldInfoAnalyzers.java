package com.reedelk.plugin.service.module.impl.component.scanner.property;

import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.service.module.impl.component.scanner.ComponentAnalyzerContext;
import io.github.classgraph.FieldInfo;

import java.util.Arrays;
import java.util.List;

public class FieldInfoAnalyzers {

    private FieldInfoAnalyzers() {
    }

    private static final List<FieldInfoAnalyzer> FIELD_INFO_ANALYZERS = Arrays.asList(
            new NameFieldInfoAnalyzer(),
            new TypeFieldInfoAnalyzer(),
            new WhenFieldInfoAnalyzer(),
            new HintFieldInfoAnalyzer(),
            new DisplayNameFieldInfoAnalyzer(),
            new DefaultValueFieldInfoAnalyzer());

    public static ComponentPropertyDescriptor descriptor(FieldInfo propertyInfo, ComponentAnalyzerContext context) {
        ComponentPropertyDescriptor.Builder builder = ComponentPropertyDescriptor.builder();
        FIELD_INFO_ANALYZERS.forEach(handler -> handler.handle(propertyInfo, builder, context));
        return builder.build();
    }
}
