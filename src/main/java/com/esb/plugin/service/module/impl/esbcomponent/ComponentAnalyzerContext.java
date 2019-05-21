package com.esb.plugin.service.module.impl.esbcomponent;

import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

public class ComponentAnalyzerContext {

    private final ScanResult scanResult;

    public ComponentAnalyzerContext(ScanResult scanResult) {
        this.scanResult = scanResult;
    }

    ClassInfo getClassInfo(String fullyQualifiedClassName) {
        return scanResult.getClassInfo(fullyQualifiedClassName);
    }
}
