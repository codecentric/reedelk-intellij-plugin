package com.esb.plugin.service.module.impl;

import com.esb.plugin.component.ComponentDescriptor;
import com.esb.plugin.service.module.ComponentService;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ComponentServiceImpl implements ComponentService {

    private ComponentScanner componentScanner = new ComponentScanner();

    // TODO: This should not be hardcoded here.
    public static final String SYSTEM_COMPONENTS_BASE_PACKAGE = "com.esb.component";


    private final Module module;
    private Set<ComponentDescriptor> componentDescriptors = new HashSet<>();

    // TODO: each path should scan stuff...

    public ComponentServiceImpl(Module module) {
        this.module = module;
    }

    @Override
    public void syncScanComponents() {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        CompletableFuture<Void> systemComponents = componentScanner.scanPackages(componentDescriptors ->
                        ComponentServiceImpl.this.componentDescriptors.addAll(componentDescriptors),
                SYSTEM_COMPONENTS_BASE_PACKAGE);
        futures.add(systemComponents);

        String[] classPathEntries = ModuleRootManager.getInstance(module)
                .orderEntries()
                .withoutSdk()
                .withoutDepModules()
                .librariesOnly()
                .classes()
                .getUrls();
        for (String classPathEntry : classPathEntries) {
            CompletableFuture<Void> componentFuture = componentScanner.scan(componentDescriptors ->
                            ComponentServiceImpl.this.componentDescriptors.addAll(componentDescriptors),
                    classPathEntry);
            futures.add(componentFuture);
        }

        String[] localProject = ModuleRootManager.getInstance(module)
                .orderEntries()
                .withoutSdk()
                .withoutLibraries()
                .classes()
                .getUrls();
        for (String localClassPathEntry : localProject) {
            CompletableFuture<Void> componentFuture = componentScanner.scan(componentDescriptors ->
                            ComponentServiceImpl.this.componentDescriptors.addAll(componentDescriptors),
                    localClassPathEntry);
            futures.add(componentFuture);
        }

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println("End");
    }

    @Override
    public ComponentDescriptor componentDescriptorByName(String componentFullyQualifiedName) {
        Optional<ComponentDescriptor> first = componentDescriptors.stream()
                .filter(descriptor ->
                        componentFullyQualifiedName.equals(descriptor.getFullyQualifiedName()))
                .findFirst();

        // TODO: if it does not exists, must return unknown component
        if (first.isPresent()) {
            return first.get();
        } else {
            throw new RuntimeException("Not present " + componentFullyQualifiedName);
        }
    }

    @Override
    public Set<ComponentDescriptor> listComponents() {
        return Collections.unmodifiableSet(componentDescriptors);
    }

}
