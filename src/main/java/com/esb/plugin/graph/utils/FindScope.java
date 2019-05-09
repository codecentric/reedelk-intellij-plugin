package com.esb.plugin.graph.utils;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;

import java.util.Optional;

public class FindScope {

    public static Optional<ScopedGraphNode> of(FlowGraph graph, GraphNode target) {
        return graph.nodes()
                .stream()
                .filter(node -> node instanceof ScopedGraphNode)
                .map(node -> (ScopedGraphNode) node)
                .filter(scopedNode -> scopedNode.scopeContains(target))
                .findFirst();
    }
}
