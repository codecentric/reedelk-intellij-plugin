package com.reedelk.plugin.graph.utils;

import com.reedelk.plugin.commons.IsScopedGraphNode;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;

import java.util.Optional;

public class BelongToSameScope {

    private BelongToSameScope() {
    }

    public static boolean from(FlowGraph graph, GraphNode node1, GraphNode node2) {
        Optional<ScopedGraphNode> scope1 = IsScopedGraphNode.of(node1) ? Optional.of((ScopedGraphNode) node1) : FindScope.of(graph, node1);
        Optional<ScopedGraphNode> scope2 = IsScopedGraphNode.of(node2) ? Optional.of((ScopedGraphNode) node2) : FindScope.of(graph, node2);

        if (!scope1.isPresent() && !scope2.isPresent()) {
            // they both don't belong to ANY scope.
            return true;
        }

        if (scope1.isPresent() && scope2.isPresent()) {
            return scope1.get() == scope2.get();
        }

        return false;
    }

}
