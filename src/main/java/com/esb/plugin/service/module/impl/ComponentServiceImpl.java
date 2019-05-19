package com.esb.plugin.service.module.impl;

import com.esb.plugin.component.ComponentDescriptor;
import com.esb.plugin.service.module.ComponentService;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import static com.esb.internal.commons.ModuleProperties.Bundle;

public class ComponentServiceImpl implements ComponentService {

    // TODO: This is wrong.... should be removed
    private static final String SYSTEM_COMPONENTS_BASE_PACKAGE = "com.esb.component";

    private final Module module;
    private final Project project;

    private ComponentScanner componentScanner = new ComponentScanner();
    private Set<ComponentDescriptor> componentDescriptors = new HashSet<>();

    public ComponentServiceImpl(Project project, Module module) {
        this.project = project;
        this.module = module;
    }

    @Override
    public void syncScanComponents() {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        CompletableFuture<Void> systemComponents = componentScanner.scanPackages(componentDescriptors ->
                        ComponentServiceImpl.this.componentDescriptors.addAll(componentDescriptors),
                SYSTEM_COMPONENTS_BASE_PACKAGE);
        futures.add(systemComponents);

        List<String> classPathEntries = ModuleRootManager.getInstance(module)
                .orderEntries()
                .withoutSdk()
                .withoutDepModules()
                .librariesOnly()
                .classes()
                .getPathsList()
                .getPathList();


        scanClassPathEntries(classPathEntries.stream()
                .filter(ComponentServiceImpl::isESBModule)
                .toArray(String[]::new), futures);


        String[] currentProjectClassPathEntries = ModuleRootManager.getInstance(module)
                .orderEntries()
                .withoutSdk()
                .withoutLibraries()
                .classes()
                .getUrls();
        scanClassPathEntries(currentProjectClassPathEntries, futures);

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
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

    private void scanClassPathEntries(String[] classPathEntries, List<CompletableFuture<Void>> futures) {
        Arrays.stream(classPathEntries).forEach(classPathEntry -> {
            CompletableFuture<Void> componentFuture = componentScanner.scan(componentDescriptors ->
                            ComponentServiceImpl.this.componentDescriptors.addAll(componentDescriptors),
                    classPathEntry);
            futures.add(componentFuture);
        });
    }

    private static boolean isESBModule(String jarFilePath) {
        try {
            JarFile jarFile = new JarFile(new File(jarFilePath));
            Manifest manifest = jarFile.getManifest();
            Attributes mainAttributes = manifest.getMainAttributes();
            String isEsbModule = mainAttributes.getValue(Bundle.MODULE_HEADER_NAME);
            return Boolean.parseBoolean(isEsbModule);
        } catch (Exception e) {
            return false;
        }
    }

}
