package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;

import java.util.Optional;

public interface DiscoveryStrategy {

    Optional<PreviousComponentOutput> computeForScope(ComponentContext context, GraphNode nodeWeWantOutputFrom);

    Optional<PreviousComponentOutput> computeForScope(ComponentContext context, ScopedGraphNode scopedGraphNode);
}
