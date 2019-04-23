package com.esb.plugin.reflection;

import com.esb.api.component.Component;
import com.esb.plugin.designer.editor.component.ComponentDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImplementorScanner {

    // TODO: This should not be hardcoded here.
    private static final String COMPONENT_ANNOTATION_NAME = "org.osgi.service.component.annotations.Component";
    private static final String COMPONENT_SUPERCLASS = Component.class.getName();

    // TODO: each path should scan stuff...
    public List<ComponentDescriptor> listComponents(Project project, VirtualFile file) {
        Module module = ModuleUtil.findModuleForFile(file, project);
        String[] classPathEntries = ModuleRootManager.getInstance(module).orderEntries().withoutSdk().classes().getUrls();

        Iterable<String> pathElements = Arrays.asList(classPathEntries);

        ScanResult scanResult = new ClassGraph().overrideClasspath(pathElements)
                .enableSystemJarsAndModules()
                .enableAllInfo()
                .scan();

        ClassInfoList components = scanResult.getClassesWithAnnotation(COMPONENT_ANNOTATION_NAME);
        List<ComponentDescriptor> descriptors = new ArrayList<>();
        for (ClassInfo component : components) {
            if (implementsComponentSuperclazz(component)) {
                ComponentDescriptor descriptor = new ComponentDescriptor(component);
                descriptors.add(descriptor);
            }
        }
        return descriptors;
    }

    private boolean implementsComponentSuperclazz(ClassInfo component) {
        ClassInfoList interfaces = component.getInterfaces();
        for (ClassInfo theInterface : interfaces) {
            if (theInterface.getName().equals(COMPONENT_SUPERCLASS)) {
                return true;
            }
        }
        return false;
    }
}
