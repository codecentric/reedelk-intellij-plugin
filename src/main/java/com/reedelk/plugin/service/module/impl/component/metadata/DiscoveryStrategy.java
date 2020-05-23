package com.reedelk.plugin.service.module.impl.component.metadata;

import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.TypeDefault;

import java.util.Optional;

import static java.util.Collections.singletonList;

public interface DiscoveryStrategy {

    Optional<PreviousComponentOutput> compute(ComponentContext context, GraphNode nodeWeWantOutputFrom);

    // Method to be used to compute the output of nodes right outside the scoped graph node.
    Optional<PreviousComponentOutput> compute(ComponentContext context, ScopedGraphNode scopedGraphNode);

    PreviousComponentOutputDefault DEFAULT =
            new PreviousComponentOutputDefault(
                    singletonList(TypeDefault.DEFAULT_ATTRIBUTES),
                    singletonList(TypeDefault.DEFAULT_PAYLOAD));
}
