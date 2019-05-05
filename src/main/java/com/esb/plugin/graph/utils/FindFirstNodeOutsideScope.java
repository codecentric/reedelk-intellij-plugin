package com.esb.plugin.graph.utils;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

/**
 * It finds the first node outside the given ScopedGraphNode.
 * By definition a scope block must be followed only by one node.
 */
public class FindFirstNodeOutsideScope {

    public static Optional<GraphNode> of(FlowGraph graph, ScopedGraphNode scope) {
        Set<GraphNode> firstDrawablesOutsideScope = new HashSet<>();

        ListLastNodeOfScope.from(graph, scope)
                .forEach(lastDrawableOfScope -> {
                    List<GraphNode> successors = graph.successors(lastDrawableOfScope);
                    firstDrawablesOutsideScope.addAll(successors);
                });

        checkState(firstDrawablesOutsideScope.isEmpty() ||
                        firstDrawablesOutsideScope.size() == 1,
                "First node outside scope must be asent or at most one");

        return firstDrawablesOutsideScope.stream().findFirst();
    }
}
