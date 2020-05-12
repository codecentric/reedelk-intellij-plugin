package com.reedelk.plugin.component.type.placeholder;

import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.component.type.generic.GenericComponentDiscovery;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.impl.component.PlatformComponentServiceImpl;

import java.util.Optional;

public class PlaceholderComponentDiscovery extends GenericComponentDiscovery {

    public PlaceholderComponentDiscovery(PlatformComponentServiceImpl componentService) {
        super(componentService);
    }

    @Override
    public Optional<? extends ComponentOutputDescriptor> compute(ContainerContext context, GraphNode predecessor) {
        return Optional.empty();
    }
}
