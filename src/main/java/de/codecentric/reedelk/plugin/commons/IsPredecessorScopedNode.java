package de.codecentric.reedelk.plugin.commons;

import de.codecentric.reedelk.plugin.graph.node.GraphNode;

import java.util.List;

public class IsPredecessorScopedNode {

    private IsPredecessorScopedNode() {
    }

    /**
     * If a node has as predecessor a ScopedNode, then its predecessors
     * must be at most 1 and of type ScopedGraphNode.
     *
     * @param predecessors the list of the node's we want to test predecessors.
     * @return true if the predecessor is a ScopedGraphNode, false otherwise.
     */
    public static boolean of(List<GraphNode> predecessors) {
        return predecessors.size() == 1 &&
                IsScopedGraphNode.of(predecessors.get(0));
    }
}
