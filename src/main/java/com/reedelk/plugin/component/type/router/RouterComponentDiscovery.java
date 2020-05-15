package com.reedelk.plugin.component.type.router;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.component.type.generic.GenericComponentDiscovery;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import com.reedelk.runtime.api.message.MessageAttributes;

import java.util.Collection;
import java.util.Optional;

import static java.util.Collections.singletonList;

public class RouterComponentDiscovery extends GenericComponentDiscovery {

    public RouterComponentDiscovery(Module module, PlatformModuleService moduleService, TypeAndTries typeAndAndTries) {
        super(module, moduleService, typeAndAndTries);
    }

    @Override
    public Optional<? extends ComponentOutputDescriptor> compute(ComponentContext context, GraphNode currentNode) {
        return discover(context, currentNode);
    }

    @Override
    public Optional<ComponentOutputDescriptor> compute(ComponentContext context, Collection<GraphNode> predecessors) {
        ComponentOutputDescriptor descriptor = new ComponentOutputDescriptor();
        descriptor.setPayload(singletonList(Object.class.getName()));
        // TODO: merge attributes like in fork for each route branch.
        descriptor.setAttributes(singletonList(MessageAttributes.class.getName()));
        return Optional.of(descriptor);
    }
}
