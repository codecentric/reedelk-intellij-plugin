package com.reedelk.plugin.component.domain;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Represent an ESB Module.
 * An ESB Module is a container of components with a name.
 */
public class ComponentsPackage {

    private final String name;
    private final List<ComponentDescriptor> moduleComponents;

    public ComponentsPackage(String name, List<ComponentDescriptor> moduleComponents) {
        this.name = name;
        this.moduleComponents = moduleComponents;
    }

    public String getName() {
        return name;
    }

    public List<ComponentDescriptor> getModuleComponents() {
        return Collections.unmodifiableList(moduleComponents);
    }

    public Optional<ComponentDescriptor> getModuleComponent(String fullyQualifiedName) {
        return moduleComponents.stream()
                .filter(descriptor -> descriptor.getFullyQualifiedName().equals(fullyQualifiedName))
                .findFirst();
    }
}
