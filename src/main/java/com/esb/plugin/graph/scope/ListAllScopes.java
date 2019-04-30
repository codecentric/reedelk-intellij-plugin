package com.esb.plugin.graph.scope;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.ScopedNode;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class ListAllScopes {

    public static List<ScopedNode> of(FlowGraph graph) {
        return graph.nodes()
                .stream()
                .filter(node -> node instanceof ScopedNode)
                .map(node -> (ScopedNode) node)
                .collect(toList());
    }
}
