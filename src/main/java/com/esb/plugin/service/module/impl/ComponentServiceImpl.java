package com.esb.plugin.service.module.impl;

import com.esb.component.Stop;
import com.esb.plugin.commons.ESBModuleInfo;
import com.esb.plugin.component.ComponentDescriptor;
import com.esb.plugin.component.unknown.UnknownComponentDescriptor;
import com.esb.plugin.service.module.ComponentService;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class ComponentServiceImpl implements ComponentService {

    private final Module module;

    private ComponentScanner componentScanner = new ComponentScanner();
    private Map<String, ComponentDescriptor> allDescriptors = new HashMap<>();

    public ComponentServiceImpl(Module module) {
        this.module = module;
        componentScanner.getComponentsFromPackage(Stop.class.getPackage().getName())
                .forEach(descriptor ->
                        allDescriptors.put(descriptor.getFullyQualifiedName(), descriptor));
    }

    @Override
    public void syncScanComponents() {
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        List<String> classPathEntries = ModuleRootManager.getInstance(module)
                .orderEntries()
                .withoutSdk()
                .withoutDepModules()
                .librariesOnly()
                .classes()
                .getPathsList()
                .getPathList();


        scanClassPathEntries(classPathEntries.stream()
                .filter(ESBModuleInfo::fromJarFilePath)
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
        if (allDescriptors.containsKey(componentFullyQualifiedName)) {
            return allDescriptors.get(componentFullyQualifiedName);
        } else {
            return new UnknownComponentDescriptor();
        }
    }

    @Override
    public Set<ComponentDescriptor> listComponents() {
        return Collections.unmodifiableSet(new HashSet<>(allDescriptors.values()));
    }

    private void scanClassPathEntries(String[] classPathEntries, List<CompletableFuture<Void>> futures) {
        Arrays.stream(classPathEntries).forEach(classPathEntry -> {
            CompletableFuture<Void> componentFuture = componentScanner.scan(consumer, classPathEntry);
            futures.add(componentFuture);
        });
    }

    private Consumer<List<ComponentDescriptor>> consumer =
            componentDescriptors ->
                    componentDescriptors.forEach(descriptor ->
                            allDescriptors.put(descriptor.getFullyQualifiedName(), descriptor));

}
