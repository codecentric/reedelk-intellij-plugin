package com.reedelk.plugin.component.type.trycatch;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.impl.component.PlatformComponentService;
import com.reedelk.plugin.service.module.impl.component.completion.TrieMapWrapper;
import com.reedelk.plugin.service.module.impl.component.discovery.AbstractDiscoveryStrategy;
import com.reedelk.plugin.service.module.impl.component.discovery.DiscoveryStrategyFactory;
import com.reedelk.runtime.api.message.MessageAttributes;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TryCatchComponentDiscovery extends AbstractDiscoveryStrategy {

    public TryCatchComponentDiscovery(Module module, PlatformComponentService componentService, TrieMapWrapper typeAndAndTries) {
        super(module, componentService, typeAndAndTries);
    }

    @Override
    public Optional<? extends ComponentOutputDescriptor> compute(ContainerContext context, GraphNode predecessor) {
        List<GraphNode> successors = context.successors(predecessor);
        if (successors.get(0).equals(context.node())) {
            // Try branch (we take the one before the try-catch.
            return DiscoveryStrategyFactory.get(module, componentService, typeAndAndTries, context, predecessor);
        } else {
            // We are in the catch
            ComponentOutputDescriptor descriptor = new ComponentOutputDescriptor();
            descriptor.setAttributes(MessageAttributes.class.getName());
            descriptor.setPayload(Collections.singletonList(Exception.class.getName()));
            return Optional.of(descriptor);
        }
    }

    @Override
    public Optional<? extends ComponentOutputDescriptor> compute(ContainerContext context, Collection<GraphNode> predecessors) {
        ComponentOutputDescriptor descriptor = new ComponentOutputDescriptor();
        descriptor.setPayload(Collections.singletonList(Object.class.getName()));
        descriptor.setAttributes(MessageAttributes.class.getName());
        return Optional.of(descriptor);
    }
}
