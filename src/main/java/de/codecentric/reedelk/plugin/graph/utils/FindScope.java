package de.codecentric.reedelk.plugin.graph.utils;

import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.node.ScopedGraphNode;
import de.codecentric.reedelk.plugin.commons.IsScopedGraphNode;

import java.util.Optional;

public class FindScope {

    private FindScope() {
    }

    public static Optional<ScopedGraphNode> of(FlowGraph graph, GraphNode target) {
        return graph.nodes()
                .stream()
                .filter(IsScopedGraphNode::of)
                .map(node -> (ScopedGraphNode) node)
                .filter(scopedNode -> scopedNode.scopeContains(target))
                .findFirst();
    }
}
