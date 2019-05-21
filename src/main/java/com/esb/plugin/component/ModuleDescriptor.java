package com.esb.plugin.component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ModuleDescriptor {

    private final String name;
    private final List<ComponentDescriptor> moduleComponents;

    public ModuleDescriptor(String name, List<ComponentDescriptor> moduleComponents) {
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
