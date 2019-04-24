package com.esb.plugin.designer.editor.component;

import io.github.classgraph.ClassInfo;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.MethodInfoList;

import java.awt.datatransfer.DataFlavor;
import java.util.ArrayList;
import java.util.List;

public class ComponentDescriptor {

    public static final DataFlavor FLAVOR = new DataFlavor(ComponentDescriptor.class,
            "Descriptor of Component");

    private final ClassInfo classInfo;

    public ComponentDescriptor(ClassInfo classInfo) {
        this.classInfo = classInfo;
    }

    public String getFullyQualifiedName() {
        if (classInfo == null) {
            return "default";
        }
        return classInfo.getName();
    }

    public String getDisplayName() {
        if (classInfo == null) {
            return "default";
        }
        return classInfo.getSimpleName();
    }

    public List<MethodInfo> getProperties() {
        MethodInfoList methodInfos = classInfo.getMethodInfo();
        List<MethodInfo> properties = new ArrayList<>();
        for (MethodInfo methodInfo : methodInfos) {
            if (methodInfo.getName().startsWith("set")) {
                properties.add(methodInfo);
            }
        }
        return properties;
    }

}

