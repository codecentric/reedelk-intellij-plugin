package com.esb.plugin.graph.utils;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.ScopedGraphNode;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class ListAllScopes {

    public static List<ScopedGraphNode> of(FlowGraph graph) {
        return graph.nodes()
                .stream()
                .filter(node -> node instanceof ScopedGraphNode)
                .map(node -> (ScopedGraphNode) node)
                .collect(toList());
    }
}
