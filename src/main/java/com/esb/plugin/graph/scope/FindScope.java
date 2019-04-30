package com.esb.plugin.graph.scope;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedNode;

import java.util.Optional;

public class FindScope {

    public static Optional<ScopedNode> of(FlowGraph graph, GraphNode target) {
        return graph.nodes()
                .stream()
                .filter(drawable -> drawable instanceof ScopedNode)
                .map(drawable -> (ScopedNode) drawable)
                .filter(scopedDrawable -> scopedDrawable.scopeContains(target))
                .findFirst();
    }
}
