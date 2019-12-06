package com.reedelk.plugin.service.module.impl.component.scanner;

import com.reedelk.plugin.service.module.impl.component.scanner.property.ComponentPropertyAnalyzer;
import io.github.classgraph.ScanResult;

public class ComponentAnalyzerFactory {

    private ComponentAnalyzerFactory() {
    }

    public static ComponentAnalyzer get(ScanResult scanResult) {
        ComponentIconAndImageProvider.loadFrom(scanResult);
        ComponentAnalyzerContext context = new ComponentAnalyzerContext(scanResult);
        ComponentPropertyAnalyzer propertyAnalyzer = new ComponentPropertyAnalyzer(context);
        return new ComponentAnalyzer(context, propertyAnalyzer);
    }
}
