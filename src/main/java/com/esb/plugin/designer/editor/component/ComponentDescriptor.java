package com.esb.plugin.designer.editor.component;

import io.github.classgraph.ClassInfo;

import java.awt.datatransfer.DataFlavor;

public class ComponentDescriptor {

    public static final DataFlavor FLAVOR = new DataFlavor(ComponentDescriptor.class,
            "Descriptor of Component");

    private final ClassInfo classInfo;
    private final String name; // TODO: To be removed

    public ComponentDescriptor(String name) {
        this.classInfo = null;
        this.name = name;
    }

    public ComponentDescriptor(ClassInfo classInfo) {
        this.classInfo = classInfo;
        this.name = null;
    }

    public String getFullyQualifiedName() {
        return classInfo.getName();
    }

    public String getDisplayName() {
        return classInfo == null ? name : classInfo.getSimpleName();
    }

}

