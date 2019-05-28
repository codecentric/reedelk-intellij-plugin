package com.esb.plugin.graph.layout;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.utils.CountNestedScopes;
import com.esb.plugin.graph.utils.FindFirstNodeOutsideScope;

import java.awt.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.esb.plugin.graph.node.ScopedGraphNode.HORIZONTAL_PADDING;
import static com.esb.plugin.graph.node.ScopedGraphNode.VERTICAL_PADDING;
import static com.google.common.base.Preconditions.checkState;

public class FlowGraphLayoutUtils {

    static int maxHeight(FlowGraph graph, Graphics2D graphics, GraphNode start) {
        return maxHeight(graph, graphics, start, null);
    }

    public static int maxHeight(FlowGraph graph, Graphics2D graphics, GraphNode start, GraphNode end) {
        return maxHeight(graphics, graph, start, end, 0);
    }

    private static int maxHeight(Graphics2D graphics, FlowGraph graph, GraphNode start, GraphNode end, int currentMax) {
        if (start == end) {
            return currentMax;

        } else if (start instanceof ScopedGraphNode) {
            return maxHeight(graphics, graph, (ScopedGraphNode) start, end, currentMax);

        } else {

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
    }

    private static int maxHeight(Graphics2D graphics, FlowGraph graph, ScopedGraphNode scopedGraphNode, GraphNode end, int currentMax) {
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

        } else {
            // If there are many successors, they can not
            // be (by definition) inside the scope.
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


    static int findContainingLayer(List<List<GraphNode>> layers, GraphNode current) {
        for (int i = 0; i < layers.size(); i++) {
            if (layers.get(i).contains(current)) {
                return i;
            }
        }
        throw new RuntimeException("Could not find containing layer for node " + current);
    }

    static int layerWidthSumPreceding(FlowGraph graph, Graphics2D graphics, List<List<GraphNode>> layers, int precedingLayerIndex) {
        int sum = 0;
        for (int i = 0; i < precedingLayerIndex; i++) {
            List<GraphNode> layerDrawables = layers.get(i);
            sum += maxLayerWidth(graph, graphics, layerDrawables);
        }
        return sum;
    }

    static GraphNode findCommonParent(FlowGraph graph, Collection<GraphNode> nodes) {
        Set<GraphNode> commonParents = new HashSet<>();
        nodes.forEach(node -> commonParents.addAll(graph.predecessors(node)));
        checkState(commonParents.size() == 1, "Common parent must be one");
        return commonParents.stream().findFirst().get();
    }

    private static int maxLayerWidth(FlowGraph graph, Graphics2D graphics, List<GraphNode> layerNodes) {
        int max = 0;
        for (GraphNode layerNode : layerNodes) {
            int nestedScopes = CountNestedScopes.of(graph, layerNode);
            int total = layerNode.width(graphics) + nestedScopes * HORIZONTAL_PADDING;
            if (total > max) max = total;
        }
        return max;
    }
}
