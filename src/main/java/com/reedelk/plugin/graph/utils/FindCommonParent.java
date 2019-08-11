package com.reedelk.plugin.graph.utils;

import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

public class FindCommonParent {

    private FindCommonParent() {
    }

    public static GraphNode of(FlowGraph graph, Collection<GraphNode> nodes) {
        Set<GraphNode> commonParents = new HashSet<>();

        nodes.forEach(node -> commonParents.addAll(graph.predecessors(node)));

        checkState(commonParents.size() == 1, "Common parent must be one");

        return commonParents.iterator().next();
    }
}
