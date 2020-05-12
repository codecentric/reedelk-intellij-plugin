package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;

import java.util.Collection;
import java.util.Optional;

public interface DiscoveryStrategy {

    Optional<? extends ComponentOutputDescriptor> compute(ComponentContext context, GraphNode nodeWeWantOutputFrom);

    default Optional<? extends ComponentOutputDescriptor> compute(ComponentContext context, Collection<GraphNode> predecessors) {
        throw new UnsupportedOperationException();
    }
}
