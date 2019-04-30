package com.esb.plugin.graph.scope;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedDrawable;

import java.util.Optional;

public class FindScope {

    public static Optional<ScopedDrawable> of(FlowGraph graph, GraphNode target) {
        return graph.nodes()
                .stream()
                .filter(drawable -> drawable instanceof ScopedDrawable)
                .map(drawable -> (ScopedDrawable) drawable)
                .filter(scopedDrawable -> scopedDrawable.scopeContains(target))
                .findFirst();
    }
}
