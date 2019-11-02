package com.reedelk.plugin.graph.utils;

import com.reedelk.plugin.commons.IsPredecessorScopedNode;
import com.reedelk.plugin.component.type.stop.StopNode;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;

import java.util.List;
import java.util.Optional;

/**
 * A stop node always follows a scoped node. This function finds the ScopedNode,
 * the stop node is referring to.
 */
public class FindRelatedScopeOfStopNode {

    private FindRelatedScopeOfStopNode() {
    }

    public static GraphNode find(FlowGraph graph, StopNode stopNode) {

        List<GraphNode> predecessors = graph.predecessors(stopNode);
        if (IsPredecessorScopedNode.of(predecessors)) {
            return predecessors.get(0);
        }

        Optional<ScopedGraphNode> scope = FindScope.of(graph, predecessors.get(0));
        if (!scope.isPresent()) {
            throw new IllegalStateException("Expected at least one scope preceding stop node");
        }

        return scope.get();
    }
}
