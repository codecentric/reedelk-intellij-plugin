package com.esb.plugin.graph.layout.utils;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.utils.FindFirstNodeOutsideScope;

import java.awt.*;
import java.util.List;

import static com.esb.plugin.graph.node.ScopedGraphNode.VERTICAL_PADDING;
import static com.google.common.base.Preconditions.checkState;

public class ComputeMaxHeight {

    private ComputeMaxHeight() {
    }

    public static int of(FlowGraph graph, Graphics2D graphics, GraphNode start, GraphNode end) {
        return maxHeight(graphics, graph, start, end, 0);
    }

    public static int of(FlowGraph graph, Graphics2D graphics, GraphNode start) {
        return of(graph, graphics, start, null);
    }

    private static int maxHeight(Graphics2D graphics, FlowGraph graph, GraphNode start, GraphNode end, int currentMax) {
        if (start == end) {
            return currentMax;
        } else if (start instanceof ScopedGraphNode) {
            return maxHeightOfScopeSubtree(graphics, graph, (ScopedGraphNode) start, end, currentMax);
        } else {
            return maxHeightOfSubtree(graphics, graph, start, end, currentMax);
        }
    }

    private static int maxHeightOfSubtree(Graphics2D graphics, FlowGraph graph, GraphNode start, GraphNode end, int currentMax) {
        int newMax = currentMax > start.height(graphics) ? currentMax : start.height(graphics);
        List<GraphNode> successors = graph.successors(start);
        checkState(successors.size() == 1 || successors.isEmpty(),
                "Zero or at most one successors expected");

        if (successors.isEmpty()) {
            return newMax;
        }

        GraphNode successor = successors.get(0);
        return maxHeight(graphics, graph, successor, end, newMax);
    }

    private static int maxHeightOfScopeSubtree(Graphics2D graphics, FlowGraph graph, ScopedGraphNode scopedGraphNode, GraphNode end, int currentMax) {
        List<GraphNode> successors = graph.successors(scopedGraphNode);

        GraphNode firstNodeOutsideScope = FindFirstNodeOutsideScope.of(graph, scopedGraphNode).orElse(null);

        int sum = VERTICAL_PADDING + VERTICAL_PADDING;

        // If this scope does not have successors, the sum is its height.
        if (successors.isEmpty()) {
            sum += scopedGraphNode.height(graphics);

        } else if (successors.size() == 1 && successors.get(0) == firstNodeOutsideScope) {
            // If this scope has just one successor which is right outside the scope,
            // then, the sum is its height.
            sum += scopedGraphNode.height(graphics);

        } else if (successors.size() == 1) {
            // If this scope has just one successor which is inside the scope,
            // then the sum is the max subtree of either the current scoped node or the subtree.
            // This case is when there is a scope node with just one successor in the scope.
            // Note that the single successor in the scope might be another scoped node as well.
            int subtreeMax = maxHeight(graphics, graph, successors.get(0), firstNodeOutsideScope, 0);
            int scopeNodeMax = scopedGraphNode.height(graphics);
            sum += subtreeMax > scopeNodeMax ? subtreeMax : scopeNodeMax;

        } else {
            // If there are many successors, they can not
            // be (by definition) outside the scope.
            for (GraphNode successor : successors) {
                // We are looking for the max in the subtree starting from this successor.
                // Therefore the current max starts again from 0.
                sum += maxHeight(graphics, graph, successor, firstNodeOutsideScope, 0);
            }
        }

        int followingMax = 0;
        if (firstNodeOutsideScope != end && firstNodeOutsideScope != null) {
            followingMax = maxHeight(graphics, graph, firstNodeOutsideScope, null, 0);
        }

        int newCurrentMax = sum > currentMax ? sum : currentMax;
        return followingMax > newCurrentMax ? followingMax : newCurrentMax;
    }
}