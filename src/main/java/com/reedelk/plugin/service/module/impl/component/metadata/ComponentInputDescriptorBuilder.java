package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.module.descriptor.model.component.ComponentDescriptor;
import com.reedelk.module.descriptor.model.component.ComponentInputDescriptor;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;

import java.util.Optional;

public class ComponentInputDescriptorBuilder {

    private final PlatformModuleService moduleService;

    public ComponentInputDescriptorBuilder(PlatformModuleService moduleService) {
        this.moduleService = moduleService;
    }

    public Optional<ComponentInputDescriptor> build(ComponentContext context) {
        GraphNode componentGraphNode = context.node();
        String componentFullyQualifiedName =
                componentGraphNode.componentData().getFullyQualifiedName();
        ComponentDescriptor actualComponentDescriptor =
                moduleService.componentDescriptorOf(componentFullyQualifiedName);

        return Optional.ofNullable(actualComponentDescriptor.getInput());
    }
}
