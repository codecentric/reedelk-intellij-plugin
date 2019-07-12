package com.esb.plugin.component.scanner;

import com.esb.plugin.component.scanner.property.ComponentPropertyAnalyzer;
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
