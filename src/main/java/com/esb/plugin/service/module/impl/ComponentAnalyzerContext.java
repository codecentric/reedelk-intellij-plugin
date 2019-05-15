package com.esb.plugin.service.module.impl;

import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

class ComponentAnalyzerContext {

    private final ScanResult scanResult;

    ComponentAnalyzerContext(ScanResult scanResult) {
        this.scanResult = scanResult;
    }

    ClassInfo getClassInfo(String fullyQualifiedClassName) {
        return scanResult.getClassInfo(fullyQualifiedClassName);
    }
}
