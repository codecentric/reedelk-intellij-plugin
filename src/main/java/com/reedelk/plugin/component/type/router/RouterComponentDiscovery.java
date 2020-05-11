package com.reedelk.plugin.component.type.router;

import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.component.type.generic.GenericComponentDiscovery;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.impl.component.PlatformComponentServiceImpl;

import java.util.Collection;
import java.util.Optional;

public class RouterComponentDiscovery extends GenericComponentDiscovery {

    public RouterComponentDiscovery(PlatformComponentServiceImpl componentService) {
        super(componentService);
    }

    @Override
    public Optional<ComponentOutputDescriptor> compute(ContainerContext context, GraphNode predecessor) {
        GraphNode predecessorOfRouter = context.predecessor(predecessor);
        return super.compute(context, predecessorOfRouter);
    }

    @Override
    public Optional<ComponentOutputDescriptor> compute(ContainerContext context, Collection<GraphNode> predecessors) {
        return Optional.empty();
    }
}
