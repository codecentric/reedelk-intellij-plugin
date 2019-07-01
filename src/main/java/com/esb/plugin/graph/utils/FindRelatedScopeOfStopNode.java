package com.esb.plugin.graph.utils;

import com.esb.internal.commons.Preconditions;
import com.esb.plugin.component.type.stop.StopNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;

import java.util.List;
import java.util.Optional;

/**
 * A stop node always follows a scoped node. This function find t
 */
public class FindRelatedScopeOfStopNode {

    public static GraphNode find(FlowGraph graph, StopNode stopNode) {
        List<GraphNode> predecessors = graph.predecessors(stopNode);
        if (predecessors.get(0) instanceof ScopedGraphNode) {
            return predecessors.get(0);
        }
        Optional<ScopedGraphNode> scope = FindScope.of(graph, predecessors.get(0));
        Preconditions.checkState(scope.isPresent(), "Expected a scope");
        return scope.get();
    }
}
