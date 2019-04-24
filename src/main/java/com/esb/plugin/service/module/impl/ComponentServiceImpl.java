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
import org.apache.commons.collections.set.UnmodifiableSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import static java.util.Arrays.asList;

public class ComponentServiceImpl implements ComponentService {

    // TODO: This should not be hardcoded here.
    private static final String COMPONENT_ANNOTATION_NAME = "org.osgi.service.component.annotations.Component";
    private static final String COMPONENT_SUPERCLASS = Component.class.getName();
    private final Module module;
    private Set<ComponentDescriptor> componentDescriptors = new HashSet<>();

    // TODO: each path should scan stuff...

    public ComponentServiceImpl(Module module) {
        this.module = module;
    }

    @Override
    public void asyncFindAllComponents(Consumer<Collection<ComponentDescriptor>> callback) {
        if (componentDescriptors.isEmpty()) {
            new Thread(() -> {
                populateComponents();
                callback.accept(UnmodifiableSet.decorate(componentDescriptors));
            }).start();
        } else {
            callback.accept(UnmodifiableSet.decorate(componentDescriptors));
        }
    }

    @Override
    public ComponentDescriptor componentDescriptorByName(String componentFullyQualifiedName) {
        if (componentDescriptors.isEmpty()) {
            populateComponents();
        }

        Optional<ComponentDescriptor> first = componentDescriptors.stream()
                .filter(descriptor ->
                        componentFullyQualifiedName.equals(descriptor.getFullyQualifiedName()))
                .findFirst();

        // TODO: if it does not exists, must return unknown component
        return first.get();
    }

    private void populateComponents() {
        String[] classPathEntries = ModuleRootManager.getInstance(module).orderEntries().withoutSdk().classes().getUrls();
        Iterable<String> pathElements = asList(classPathEntries);
        ScanResult scanResult = new ClassGraph().overrideClasspath(pathElements)
                .enableSystemJarsAndModules()
                .enableAllInfo()
                .scan();

        ClassInfoList components = scanResult.getClassesWithAnnotation(COMPONENT_ANNOTATION_NAME);
        for (ClassInfo component : components) {
            if (implementsComponentSuperclazz(component)) {
                ComponentDescriptor descriptor = new ComponentDescriptor(component);
                componentDescriptors.add(descriptor);
            }
        }
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
