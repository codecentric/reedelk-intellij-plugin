package com.reedelk.plugin.commons;

import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;

import java.util.List;

public class GetSuccessorIndex {

    private GetSuccessorIndex() {
    }

    public static int ofScopedNode(FlowGraph graph, ScopedGraphNode scopedNode, GraphNode target) {
        List<GraphNode> successors = graph.successors(scopedNode);
        for (int i = 0; i < successors.size(); i++) {
            if (successors.get(i) == target) return i;
        }
        // This is the case where we need to find a toRemove index
        // for a scoped predecessor without successors in the scope.
        // In this case, the index is just 0 since it is the first to be connected.
        return 0;
    }
}
