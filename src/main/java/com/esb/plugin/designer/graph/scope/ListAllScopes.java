package com.esb.plugin.designer.graph.scope;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;

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
