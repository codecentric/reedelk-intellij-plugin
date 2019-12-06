package com.reedelk.plugin.service.module.impl.component.scanner.property;

import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.service.module.impl.commons.ScannerUtil;
import com.reedelk.plugin.service.module.impl.component.scanner.ComponentAnalyzerContext;
import io.github.classgraph.FieldInfo;

import java.util.Optional;

public class ComponentPropertyAnalyzer {

    private final ComponentAnalyzerContext context;

    public ComponentPropertyAnalyzer(ComponentAnalyzerContext context) {
        this.context = context;
    }

    public Optional<ComponentPropertyDescriptor> analyze(FieldInfo fieldInfo) {
        return ScannerUtil.isVisibleProperty(fieldInfo) ?
                Optional.of(FieldInfoAnalyzers.descriptor(fieldInfo, context)) :
                Optional.empty();
    }
}
