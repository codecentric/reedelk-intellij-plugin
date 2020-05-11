package com.reedelk.plugin.service.module.impl.component.discovery;

import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.graph.node.GraphNode;

import java.util.Collection;
import java.util.Optional;

public interface DiscoveryStrategy {

    Optional<ComponentOutputDescriptor> compute(ContainerContext context, GraphNode predecessor);

    default Optional<ComponentOutputDescriptor> compute(ContainerContext context, Collection<GraphNode> predecessors) {
        throw new UnsupportedOperationException();
    }
}
