package com.esb.plugin.component.scanner;

import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

import javax.swing.*;
import java.awt.*;

public class ComponentAnalyzerContext {

    private final ScanResult scanResult;
    private final ComponentIconsAnalyzer componentIconsAnalyzer;

    public ComponentAnalyzerContext(ScanResult scanResult, ComponentIconsAnalyzer componentIconsAnalyzer) {
        this.scanResult = scanResult;
        this.componentIconsAnalyzer = componentIconsAnalyzer;
    }

    ClassInfo getClassInfo(String fullyQualifiedClassName) {
        return scanResult.getClassInfo(fullyQualifiedClassName);
    }

    Image getImageByClassName(String fullyQualifiedClassName) {
        return componentIconsAnalyzer.getImageByFullyQualifiedName(fullyQualifiedClassName);
    }

    public Icon getIconByClassName(String fullyQualifiedClassName) {
        return componentIconsAnalyzer.getIconByFullyQualifiedName(fullyQualifiedClassName);
    }
}
