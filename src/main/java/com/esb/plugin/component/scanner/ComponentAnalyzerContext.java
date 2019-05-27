package com.esb.plugin.component.scanner;

import com.esb.plugin.commons.Icons;
import com.esb.plugin.commons.Images;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

import javax.swing.*;
import java.awt.*;

class ComponentAnalyzerContext {

    private final ScanResult scanResult;

    ComponentAnalyzerContext(ScanResult scanResult) {
        this.scanResult = scanResult;
    }

    ClassInfo getClassInfo(String fullyQualifiedClassName) {
        return scanResult.getClassInfo(fullyQualifiedClassName);
    }

    Image getImageByComponentQualifiedName(String fullyQualifiedClassName) {
        return Images.Component.get(fullyQualifiedClassName);
    }

    Icon getIconByComponentQualifiedName(String fullyQualifiedClassName) {
        return Icons.Component.get(fullyQualifiedClassName);
    }
}
