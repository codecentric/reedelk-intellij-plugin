package com.reedelk.plugin.graph.utils;

import com.reedelk.plugin.commons.IsScopedGraphNode;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.ScopedGraphNode;

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
