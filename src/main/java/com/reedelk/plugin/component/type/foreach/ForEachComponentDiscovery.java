package com.reedelk.plugin.component.type.foreach;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.module.descriptor.model.component.ComponentType;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.impl.component.PlatformComponentServiceImpl;
import com.reedelk.plugin.service.module.impl.component.discovery.AbstractDiscoveryStrategy;
import com.reedelk.plugin.service.module.impl.component.discovery.MessagesComponentOutputDescriptor;
import com.reedelk.runtime.api.message.MessageAttributes;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ForEachComponentDiscovery extends AbstractDiscoveryStrategy {

    public ForEachComponentDiscovery(Module module, PlatformComponentServiceImpl componentService) {
        super(module, componentService);
    }

    @Override
    public Optional<ComponentOutputDescriptor> compute(ContainerContext context, GraphNode predecessor) {
        ComponentOutputDescriptor descriptor = new ComponentOutputDescriptor();
        descriptor.setPayload(Collections.singletonList(Object.class.getName()));
        descriptor.setAttributes(MessageAttributes.class.getName());
        return Optional.of(descriptor);
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
