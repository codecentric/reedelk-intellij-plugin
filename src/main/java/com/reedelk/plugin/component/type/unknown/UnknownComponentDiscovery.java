package com.reedelk.plugin.component.type.unknown;

import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.impl.component.PlatformComponentServiceImpl;
import com.reedelk.plugin.service.module.impl.component.discovery.AbstractDiscoveryStrategy;

import java.util.Optional;

public class UnknownComponentDiscovery extends AbstractDiscoveryStrategy {

    public UnknownComponentDiscovery(PlatformComponentServiceImpl componentService) {
        super(componentService);
    }

    @Override
    public Optional<ComponentOutputDescriptor> compute(ContainerContext context, GraphNode predecessor) {
        // The output of this component can not be determined because
        // this component is unknown, therefore we return empty.
        return Optional.empty();
    }
}
