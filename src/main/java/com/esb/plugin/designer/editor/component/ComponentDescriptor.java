package com.esb.plugin.designer.editor.component;

import io.github.classgraph.ClassInfo;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.MethodInfoList;

import java.awt.datatransfer.DataFlavor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentDescriptor {

    private Map<String, Object> componentData = new HashMap<>();

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

    public List<String> getPropertiesNames() {
        MethodInfoList methodInfos = classInfo.getMethodInfo();
        List<String> propertiesNames = new ArrayList<>();
        for (MethodInfo methodInfo : methodInfos) {
            if (methodInfo.getName().startsWith("set")) {
                propertiesNames.add(methodInfo.getName().substring(3));
            }
        }
        return propertiesNames;
    }

    public void setPropertyValue(String propertyName, Object propertyValue) {
        this.componentData.put(propertyName, propertyValue);
    }
}

