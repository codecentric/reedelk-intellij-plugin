package com.esb.plugin.designer.editor.component;

import io.github.classgraph.ClassInfo;

import java.awt.datatransfer.DataFlavor;

public class ComponentDescriptor {

    public static final DataFlavor FLAVOR = new DataFlavor(ComponentDescriptor.class,
            "Descriptor of Component");

    private final ClassInfo classInfo;

    public ComponentDescriptor(ClassInfo classInfo) {
        this.classInfo = classInfo;
    }

    public String getFullyQualifiedName() {
        return classInfo.getName();
    }

    public String getDisplayName() {
        return classInfo.getSimpleName();
    }

}

