package com.reedelk.plugin.component.type.fork;

import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.module.descriptor.model.component.ComponentType;
import com.reedelk.plugin.component.type.generic.GenericComponentDiscovery;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.impl.component.PlatformComponentServiceImpl;
import com.reedelk.plugin.service.module.impl.component.discovery.DiscoveryStrategyFactory;
import com.reedelk.plugin.service.module.impl.component.discovery.MessagesComponentOutputDescriptor;
import com.reedelk.runtime.api.message.MessageAttributes;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ForkComponentDiscovery extends GenericComponentDiscovery {

    public ForkComponentDiscovery(PlatformComponentServiceImpl componentService) {
        super(componentService);
    }

    @Override
    public Optional<? extends ComponentOutputDescriptor> compute(ContainerContext context, GraphNode predecessor) {
        return DiscoveryStrategyFactory.get(context, componentService, predecessor);
    }

    @Override
    public Optional<? extends ComponentOutputDescriptor> compute(ContainerContext context, Collection<GraphNode> predecessors) {
        ComponentType componentClass = context.node().getComponentClass();
        if (ComponentType.JOIN.equals(componentClass)) {
            MessagesComponentOutputDescriptor descriptor = new MessagesComponentOutputDescriptor();
            return Optional.of(descriptor);
        } else {
            ComponentOutputDescriptor outputDescriptor = new ComponentOutputDescriptor();
            outputDescriptor.setPayload(Collections.singletonList(List.class.getName()));
            outputDescriptor.setAttributes(MessageAttributes.class.getName());
            return Optional.of(outputDescriptor);
        }
    }
}
