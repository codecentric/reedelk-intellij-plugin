package com.reedelk.plugin.component.type.fork;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.module.descriptor.model.component.ComponentType;
import com.reedelk.plugin.component.type.generic.GenericComponentDiscovery;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.TrieMapWrapper;
import com.reedelk.plugin.service.module.impl.component.metadata.MultipleMessages;
import com.reedelk.runtime.api.message.MessageAttributes;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ForkComponentDiscovery extends GenericComponentDiscovery {

    public ForkComponentDiscovery(Module module, PlatformModuleService moduleService, TrieMapWrapper typeAndAndTries) {
        super(module, moduleService, typeAndAndTries);
    }

    @Override
    public Optional<? extends ComponentOutputDescriptor> compute(ComponentContext context, GraphNode currentNode) {
        // Skip one
        return discover(context, currentNode);
    }

    @Override
    public Optional<? extends ComponentOutputDescriptor> compute(ComponentContext context, Collection<GraphNode> predecessors) {
        ComponentType componentClass = context.node().getComponentClass();
        if (ComponentType.JOIN.equals(componentClass)) {
            MultipleMessages descriptor = new MultipleMessages();
            return Optional.of(descriptor);
        } else {
            ComponentOutputDescriptor outputDescriptor = new ComponentOutputDescriptor();
            outputDescriptor.setPayload(Collections.singletonList(List.class.getName()));
            outputDescriptor.setAttributes(MessageAttributes.class.getName());
            return Optional.of(outputDescriptor);
        }
    }
}
