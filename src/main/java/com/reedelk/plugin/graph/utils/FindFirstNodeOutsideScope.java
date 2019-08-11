package com.reedelk.plugin.graph.utils;

import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

/**
 * It finds the first node outside the given ScopedGraphNode.
 * The node found might also belong to any next wrapping scope
 * containing the current scope.
 * <p>
 * Please not that by definition a scope must be followed by
 * zero or at most one node.
 */
public class FindFirstNodeOutsideScope {

    private FindFirstNodeOutsideScope() {
    }

    public static Optional<GraphNode> of(FlowGraph graph, ScopedGraphNode scope) {
        Set<GraphNode> firstNodesOutsideScope = new HashSet<>();

        ListLastNodesOfScope.from(graph, scope)
                .forEach(lastNodeOfScope -> {
                    List<GraphNode> successors = graph.successors(lastNodeOfScope);
                    firstNodesOutsideScope.addAll(successors);
                });

        checkState(firstNodesOutsideScope.isEmpty() ||
                        firstNodesOutsideScope.size() == 1,
                "First node outside scope must be absent or at most one");

        return firstNodesOutsideScope.stream().findFirst();
    }
}
