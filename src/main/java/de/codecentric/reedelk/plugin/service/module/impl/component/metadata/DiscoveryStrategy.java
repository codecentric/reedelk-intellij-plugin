package de.codecentric.reedelk.plugin.service.module.impl.component.metadata;

import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.node.ScopedGraphNode;
import de.codecentric.reedelk.plugin.service.module.impl.component.ComponentContext;
import de.codecentric.reedelk.plugin.service.module.impl.component.completion.TypeBuiltIn;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static java.util.Collections.singletonList;

public interface DiscoveryStrategy {

    Optional<PreviousComponentOutput> compute(ComponentContext context, GraphNode nodeWeWantOutputFrom, @Nullable GraphNode successor);

    // Method to be used to compute the output of nodes right outside the scoped graph node.
    Optional<PreviousComponentOutput> compute(ComponentContext context, ScopedGraphNode scopedGraphNode);

    PreviousComponentOutputDefault DEFAULT =
            new PreviousComponentOutputDefault(
                    singletonList(TypeBuiltIn.DEFAULT_ATTRIBUTES),
                    singletonList(TypeBuiltIn.DEFAULT_PAYLOAD));
}
