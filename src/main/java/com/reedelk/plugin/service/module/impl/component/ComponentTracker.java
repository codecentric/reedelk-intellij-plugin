package com.reedelk.plugin.service.module.impl.component;

import com.reedelk.module.descriptor.model.ModuleDescriptor;
import com.reedelk.module.descriptor.model.component.ComponentDescriptor;
import com.reedelk.plugin.component.type.unknown.Unknown;
import com.reedelk.plugin.component.type.unknown.UnknownComponentDescriptorWrapper;
import com.reedelk.plugin.service.module.ComponentService;

import java.util.HashMap;
import java.util.Map;

public class ComponentTracker implements ComponentService {

    // COMPONENTS
    private final Map<String, ComponentDescriptor> flowControlModuleComponents = new HashMap<>(); // A map containing the component fully qualified name and the descriptor from flow control module
    private final Map<String, ComponentDescriptor> currentModuleComponents = new HashMap<>(); // A map containing the component fully qualified name and the descriptor from current module being developed.
    private final Map<String, ComponentDescriptor> mavenModulesComponents = new HashMap<>(); // A map containing the component fully qualified name and the descriptor from maven.

    @Override
    public ComponentDescriptor componentDescriptorFrom(String componentFullyQualifiedName) {
        // Is it a component from a maven dependency?
        ComponentDescriptor descriptor = mavenModulesComponents.getOrDefault(componentFullyQualifiedName, null);
        if (descriptor != null) return descriptor;

        // Is it a system component?
        descriptor = flowControlModuleComponents.getOrDefault(componentFullyQualifiedName, null);
        if (descriptor != null) return descriptor;

        // Is it a component in the current module being developed?
        descriptor = currentModuleComponents.getOrDefault(componentFullyQualifiedName, null);
        if (descriptor != null) return descriptor;

        // The component is not known.
        return new UnknownComponentDescriptorWrapper(Unknown.DESCRIPTOR);
    }

    public void clearMaven() {
        mavenModulesComponents.clear();
    }

    public void clearCurrent() {
        currentModuleComponents.clear();
    }

    public void registerCurrent(ModuleDescriptor moduleDescriptor) {
        moduleDescriptor.getComponents().forEach(componentDescriptor ->
                currentModuleComponents.put(componentDescriptor.getFullyQualifiedName(), componentDescriptor));
    }

    public void registerMaven(ModuleDescriptor moduleDescriptor) {
        moduleDescriptor.getComponents().forEach(componentDescriptor ->
                mavenModulesComponents.put(componentDescriptor.getFullyQualifiedName(), componentDescriptor));
    }

    public void registerFlowControl(ModuleDescriptor moduleDescriptor) {
        moduleDescriptor.getComponents().forEach(componentDescriptor ->
                flowControlModuleComponents.put(componentDescriptor.getFullyQualifiedName(), componentDescriptor));
    }
}
