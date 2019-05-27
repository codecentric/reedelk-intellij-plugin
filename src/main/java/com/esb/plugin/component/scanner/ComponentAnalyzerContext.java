package com.esb.plugin.component.scanner;

import com.esb.plugin.commons.Icons;
import com.esb.plugin.commons.Images;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

import javax.swing.*;
import java.awt.*;

public class ComponentAnalyzerContext {

    private final ScanResult scanResult;

    public ComponentAnalyzerContext(ScanResult scanResult, ComponentIconAndImageProvider componentIconAndImageProvider) {
        this.scanResult = scanResult;
    }

    public ClassInfo getClassInfo(String fullyQualifiedClassName) {
        return scanResult.getClassInfo(fullyQualifiedClassName);
    }

    public Image getImageByComponentQualifiedName(String fullyQualifiedClassName) {
        return Images.Component.get(fullyQualifiedClassName);
    }

    public Icon getIconByComponentQualifiedName(String fullyQualifiedClassName) {
        return Icons.Component.get(fullyQualifiedClassName);
    }
}
