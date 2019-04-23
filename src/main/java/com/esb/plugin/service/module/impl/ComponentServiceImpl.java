package com.esb.plugin.service.module.impl;

import com.esb.api.component.Component;
import com.esb.plugin.designer.editor.component.ComponentDescriptor;
import com.esb.plugin.service.module.ComponentService;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Arrays.asList;

public class ComponentServiceImpl implements ComponentService {

    // TODO: This should not be hardcoded here.
    private static final String COMPONENT_ANNOTATION_NAME = "org.osgi.service.component.annotations.Component";
    private static final String COMPONENT_SUPERCLASS = Component.class.getName();
    private final Module module;

    // TODO: each path should scan stuff...

    public ComponentServiceImpl(Module module) {
        this.module = module;
    }

    @Override
    public void findAllComponents(Consumer<Collection<ComponentDescriptor>> callback) {
        String[] classPathEntries = ModuleRootManager.getInstance(module).orderEntries().withoutSdk().classes().getUrls();

        Iterable<String> pathElements = asList(classPathEntries);
        new Thread(() -> {
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

            callback.accept(descriptors);
        }).start();
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
