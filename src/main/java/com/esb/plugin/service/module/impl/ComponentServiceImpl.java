package com.esb.plugin.service.module.impl;

import com.esb.component.Stop;
import com.esb.plugin.commons.ESBModuleInfo;
import com.esb.plugin.component.ComponentDescriptor;
import com.esb.plugin.component.unknown.UnknownComponentDescriptor;
import com.esb.plugin.service.module.ComponentService;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.util.messages.MessageBus;

import java.util.*;
import java.util.function.Consumer;

public class ComponentServiceImpl implements ComponentService {

    private final Module module;

    private final ComponentListUpdateNotifier publisher;
    private ComponentScanner componentScanner = new ComponentScanner();
    private Map<String, ComponentDescriptor> allDescriptors = new HashMap<>();

    /**
     * The constructor loads the system components. Synchronously.
     * All the other components are loaded asynchronously.
     *
     * @param module the module this service is referring to.
     */
    public ComponentServiceImpl(Module module, MessageBus messageBus) {
        this.module = module;
        componentScanner.getComponentsFromPackage(Stop.class.getPackage().getName())
                .forEach(descriptor ->
                        allDescriptors.put(descriptor.getFullyQualifiedName(), descriptor));

        publisher = messageBus.syncPublisher(ComponentListUpdateNotifier.TOPIC);
        asyncScanClasspathComponents();
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


    private void asyncScanClasspathComponents() {
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
                .toArray(String[]::new));


        String[] currentProjectClassPathEntries = ModuleRootManager.getInstance(module)
                .orderEntries()
                .withoutSdk()
                .withoutLibraries()
                .classes()
                .getUrls();
        scanClassPathEntries(currentProjectClassPathEntries);
    }

    private void scanClassPathEntries(String[] classPathEntries) {
        Arrays.stream(classPathEntries)
                .forEach(classPathEntry ->
                        componentScanner.scan(
                                consumer.andThen(componentDescriptors ->
                                        publisher.onComponentListUpdate()), classPathEntry));
    }

    private Consumer<List<ComponentDescriptor>> consumer =
            componentDescriptors ->
                    componentDescriptors.forEach(descriptor -> {
                        allDescriptors.put(descriptor.getFullyQualifiedName(), descriptor);
                    });

}
