package com.reedelk.plugin.graph.utils;

import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;

import java.util.Optional;

public class FindScope {

    private FindScope() {
    }

    public static Optional<ScopedGraphNode> of(FlowGraph graph, GraphNode target) {
        return graph.nodes()
                .stream()
                .filter(node -> node instanceof ScopedGraphNode)
                .map(node -> (ScopedGraphNode) node)
                .filter(scopedNode -> scopedNode.scopeContains(target))
                .findFirst();
    }
}
