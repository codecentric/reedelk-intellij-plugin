package com.reedelk.plugin.component.type.fork;

import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.component.type.generic.GenericComponentDiscovery;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.impl.component.PlatformComponentServiceImpl;

import java.util.Optional;

public class ForkComponentDiscovery extends GenericComponentDiscovery {

    public ForkComponentDiscovery(PlatformComponentServiceImpl componentService) {
        super(componentService);
    }

    @Override
    public Optional<ComponentOutputDescriptor> compute(ContainerContext context, GraphNode predecessor) {
        GraphNode predecessorOfFork = context.predecessor(predecessor);
        return super.compute(context, predecessorOfFork);
    }
}
