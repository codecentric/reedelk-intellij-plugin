package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.module.descriptor.model.component.ComponentDescriptor;
import com.reedelk.module.descriptor.model.component.ComponentInputDescriptor;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.impl.component.PlatformComponentServiceImpl;

import java.util.Optional;

public class ComponentInputDescriptorBuilder {

    private final PlatformComponentServiceImpl componentService;

    public ComponentInputDescriptorBuilder(PlatformComponentServiceImpl componentService) {
        this.componentService = componentService;
    }

    public Optional<ComponentInputDescriptor> build(ContainerContext context) {
        GraphNode componentGraphNode = context.node();
        String componentFullyQualifiedName =
                componentGraphNode.componentData().getFullyQualifiedName();
        ComponentDescriptor actualComponentDescriptor =
                componentService.componentDescriptorFrom(componentFullyQualifiedName);

        return Optional.ofNullable(actualComponentDescriptor.getInput());
    }
}
