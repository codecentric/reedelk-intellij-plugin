package com.esb.plugin.graph.scope;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.ScopedDrawable;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class ListAllScopes {

    public static List<ScopedDrawable> of(FlowGraph graph) {
        return graph.nodes()
                .stream()
                .filter(node -> node instanceof ScopedDrawable)
                .map(node -> (ScopedDrawable) node)
                .collect(toList());
    }
}
