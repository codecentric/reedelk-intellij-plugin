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
 * The node found might also belong to any next wrapping scope
 * containing the current scope.
 *
 * Please not that by definition a scope must be followed by
 * zero or at most one node.
 */
public class FindFirstNodeOutsideScope {

    public static Optional<GraphNode> of(FlowGraph graph, ScopedGraphNode scope) {
        Set<GraphNode> firstNodesOutsideScope = new HashSet<>();

        ListLastNodeOfScope.from(graph, scope)
                .forEach(lastDrawableOfScope -> {
                    List<GraphNode> successors = graph.successors(lastDrawableOfScope);
                    firstNodesOutsideScope.addAll(successors);
                });

        checkState(firstNodesOutsideScope.isEmpty() ||
                        firstNodesOutsideScope.size() == 1,
                "First node outside scope must be absent or at most one");

        return firstNodesOutsideScope.stream().findFirst();
    }
}
