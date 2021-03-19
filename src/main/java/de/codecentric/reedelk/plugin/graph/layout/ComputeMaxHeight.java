package de.codecentric.reedelk.plugin.graph.layout;

import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.node.ScopedGraphNode;
import de.codecentric.reedelk.plugin.graph.utils.FindFirstNodeOutsideScope;
import de.codecentric.reedelk.plugin.graph.utils.FindMaxBottomHalfHeight;
import de.codecentric.reedelk.plugin.graph.utils.FindMaxTopHalfHeight;
import de.codecentric.reedelk.plugin.commons.IsScopedGraphNode;

import java.awt.*;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

public class ComputeMaxHeight {

    private ComputeMaxHeight() {
    }

    public static int of(FlowGraph graph, Graphics2D graphics, GraphNode start) {
        return of(graph, graphics, start, null);
    }

    public static int of(FlowGraph graph, Graphics2D graphics, GraphNode start, GraphNode end) {
        return maxHeight(graphics, graph, start, end, 0);
    }

    private static int maxHeight(Graphics2D graphics, FlowGraph graph, GraphNode start, GraphNode end, int currentMax) {
        if (start == end) {
            return currentMax;
        } else if (IsScopedGraphNode.of(start)) {
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

        int sum = ScopedGraphNode.VERTICAL_PADDING + ScopedGraphNode.VERTICAL_PADDING;

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
            int topSubTreeMax = FindMaxTopHalfHeight.of(graph, graphics, successors.get(0), firstNodeOutsideScope);
            int bottomSubTreeMax = FindMaxBottomHalfHeight.of(graph, graphics, successors.get(0), firstNodeOutsideScope);

            int subtreeMax = topSubTreeMax + bottomSubTreeMax;
            int scopeNodeMax = scopedGraphNode.height(graphics);
            sum += subtreeMax > scopeNodeMax ? subtreeMax : scopeNodeMax;

        } else {
            // If there are many successors, they can not
            // be (by definition) outside the scope.
            for (GraphNode successor : successors) {
                // The max is given by the top subtree max + the bottom subtree max
                int topSubTreeMax = FindMaxTopHalfHeight.of(graph, graphics, successor, firstNodeOutsideScope);
                int bottomSubTreeMax = FindMaxBottomHalfHeight.of(graph, graphics, successor, firstNodeOutsideScope);
                int subtreeMax = topSubTreeMax + bottomSubTreeMax;
                sum += subtreeMax;
            }
        }

        int followingMax = 0;
        if (firstNodeOutsideScope != end && firstNodeOutsideScope != null) {
            followingMax = maxHeight(graphics, graph, firstNodeOutsideScope, end, 0);
        }

        int newCurrentMax = sum > currentMax ? sum : currentMax;
        return followingMax > newCurrentMax ? followingMax : newCurrentMax;
    }
}