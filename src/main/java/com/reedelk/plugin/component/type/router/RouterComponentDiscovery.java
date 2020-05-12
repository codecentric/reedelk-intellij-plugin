package com.reedelk.plugin.component.type.router;

import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.component.type.generic.GenericComponentDiscovery;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.impl.component.PlatformComponentServiceImpl;
import com.reedelk.plugin.service.module.impl.component.discovery.DiscoveryStrategyFactory;
import com.reedelk.runtime.api.message.MessageAttributes;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class RouterComponentDiscovery extends GenericComponentDiscovery {

    public RouterComponentDiscovery(PlatformComponentServiceImpl componentService) {
        super(componentService);
    }

    @Override
    public Optional<? extends ComponentOutputDescriptor> compute(ContainerContext context, GraphNode predecessor) {
        return DiscoveryStrategyFactory.get(context, componentService, predecessor);
    }

    @Override
    public Optional<ComponentOutputDescriptor> compute(ContainerContext context, Collection<GraphNode> predecessors) {
        ComponentOutputDescriptor descriptor = new ComponentOutputDescriptor();
        descriptor.setPayload(Collections.singletonList(Object.class.getName()));
        descriptor.setAttributes(MessageAttributes.class.getName());
        return Optional.of(descriptor);
    }
}
