package com.esb.plugin.service.module.impl.esbcomponent;

import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

import javax.swing.*;
import java.awt.*;

public class ComponentAnalyzerContext {

    private final ScanResult scanResult;
    private final IconsExtractor iconsExtractor;

    public ComponentAnalyzerContext(ScanResult scanResult, IconsExtractor iconsExtractor) {
        this.scanResult = scanResult;
        this.iconsExtractor = iconsExtractor;
    }

    ClassInfo getClassInfo(String fullyQualifiedClassName) {
        return scanResult.getClassInfo(fullyQualifiedClassName);
    }

    Image getImageByClassName(String fullyQualifiedClassName) {
        return iconsExtractor.getImageByFullyQualifiedName(fullyQualifiedClassName);
    }

    public Icon getIconByClassName(String fullyQualifiedClassName) {
        return iconsExtractor.getIconByFullyQualifiedName(fullyQualifiedClassName);
    }
}
