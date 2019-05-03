package com.esb.plugin.graph.scope;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;

import java.util.Optional;

public class FindScope {

    public static Optional<ScopedGraphNode> of(FlowGraph graph, GraphNode target) {
        return graph.nodes()
                .stream()
                .filter(drawable -> drawable instanceof ScopedGraphNode)
                .map(drawable -> (ScopedGraphNode) drawable)
                .filter(scopedDrawable -> scopedDrawable.scopeContains(target))
                .findFirst();
    }
}
