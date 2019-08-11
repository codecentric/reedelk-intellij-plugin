package com.reedelk.plugin.graph.utils;

import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;

import java.util.Optional;

public class BelongToSameScope {

    private BelongToSameScope() {
    }

    public static boolean from(FlowGraph graph, GraphNode node1, GraphNode node2) {
        Optional<ScopedGraphNode> scope1 = node1 instanceof ScopedGraphNode ? Optional.of((ScopedGraphNode) node1) : FindScope.of(graph, node1);
        Optional<ScopedGraphNode> scope2 = node2 instanceof ScopedGraphNode ? Optional.of((ScopedGraphNode) node2) : FindScope.of(graph, node2);

        if (!scope1.isPresent() && !scope2.isPresent()) {
            // they both don't belong to ANY scope.
            return true;
        }

        if (scope1.isPresent()) {
            if (scope2.isPresent()) {
                return scope1.get() == scope2.get();
            }
        }

        return false;
    }

}
