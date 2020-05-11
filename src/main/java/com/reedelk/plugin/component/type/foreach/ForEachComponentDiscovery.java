package com.reedelk.plugin.component.type.foreach;

import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.impl.component.PlatformComponentServiceImpl;
import com.reedelk.plugin.service.module.impl.component.discovery.AbstractDiscoveryStrategy;

import java.util.Optional;

public class ForEachComponentDiscovery extends AbstractDiscoveryStrategy {

    public ForEachComponentDiscovery(PlatformComponentServiceImpl componentService) {
        super(componentService);
    }

    @Override
    public Optional<ComponentOutputDescriptor> compute(ContainerContext context, GraphNode predecessor) {
        return Optional.empty();
    }
}
