package com.reedelk.plugin.service.module.impl.component;

import com.reedelk.module.descriptor.model.ComponentDescriptor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Represent an ESB Module.
 * An ESB Module is a container of components with a name.
 */
public class ModuleComponents {

    private final String name;
    private final List<ComponentDescriptor> moduleComponents;

    public ModuleComponents(String name, List<ComponentDescriptor> moduleComponents) {
        this.name = name;
        this.moduleComponents = moduleComponents;
    }

    public String getName() {
        return name;
    }

    public synchronized List<ComponentDescriptor> getModuleComponents() {
        return Collections.unmodifiableList(moduleComponents);
    }

    public synchronized void addAll(List<ComponentDescriptor> flowControlComponents) {
        this.moduleComponents.addAll(flowControlComponents);
    }

    synchronized Optional<ComponentDescriptor> getModuleComponent(String fullyQualifiedName) {
        return moduleComponents.stream()
                .filter(descriptor -> descriptor.getFullyQualifiedName().equals(fullyQualifiedName))
                .findFirst();
    }
}
