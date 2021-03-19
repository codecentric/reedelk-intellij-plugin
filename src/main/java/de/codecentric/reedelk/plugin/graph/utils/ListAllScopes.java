package de.codecentric.reedelk.plugin.graph.utils;

import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.ScopedGraphNode;
import de.codecentric.reedelk.plugin.commons.IsScopedGraphNode;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class ListAllScopes {

    private ListAllScopes() {
    }

    public static List<ScopedGraphNode> of(FlowGraph graph) {
        return graph.nodes()
                .stream()
                .filter(IsScopedGraphNode::of)
                .map(node -> (ScopedGraphNode) node)
                .collect(toList());
    }
}
