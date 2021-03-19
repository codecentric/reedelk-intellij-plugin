package de.codecentric.reedelk.plugin.commons;

import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.node.ScopeBoundaries;
import de.codecentric.reedelk.plugin.graph.node.ScopedGraphNode;

import java.awt.*;
import java.util.List;

public class ScopeUtils {

    private ScopeUtils() {
    }

    private static final int NODE_TOP_MARGIN_PERCENT = 35;
    private static final int NODE_BOTTOM_MARGIN_PERCENT = 40;

    // |-----------| yTopScopeBound (if successorIndex == 0)
    // |           |
    // |           |
    // |   top-0   |
    // |-----------| yCenterTopBound == yTopBottomBound
    // |  center-0 |
    // |-----------| yCenterBottomBound == yBottomTopBound
    // |  bottom-0 |
    // |-----------| yBottomBottomBound == yTopTopBound
    // |   top-1   |
    // |-----------| yCenterTopBound == yTopBottomBound
    // |  center-1 |
    // |-----------| yCenterBottomBound == yBottomTopBound
    // |  bottom-1 |
    // |           |
    // |           |
    // |-----------| yBottomScopeBound (if successor index == successors.length - 1)
    public static boolean isInsideTopArea(FlowGraph graph, List<GraphNode> successors, int successorIndex, ScopedGraphNode closestPrecedingNode, Point dropPoint, Graphics2D graphics) {
        GraphNode current = successors.get(successorIndex);
        // Topmost element
        if (successorIndex == 0) {
            // The top area is between top of scope rectangle and yTopBottomBound
            int yTopScopeBound = scopeMinYBound(graph, closestPrecedingNode, graphics);
            int yTopBottomBound = yTopBottomBoundOf(current, graphics);
            return dropPoint.y >= yTopScopeBound && dropPoint.y < yTopBottomBound;
        } else {
            // The top area is between the previous node yBottomTopBound and current yTopBottomBound
            GraphNode preceding = successors.get(successorIndex - 1);
            int yBottomTopBound = yBottomTopBoundOf(preceding, graphics);
            int yTopBottomBound = yTopBottomBoundOf(current, graphics);
            return dropPoint.y >= yBottomTopBound && dropPoint.y < yTopBottomBound;
        }
    }

    // |-----------| yTopScopeBound (if successorIndex == 0)
    // |           |
    // |           |
    // |   top-0   |
    // |-----------| yCenterTopBound == yTopBottomBound
    // |  center-0 |
    // |-----------| yCenterBottomBound == yBottomTopBound
    // |  bottom-0 |
    // |-----------| yBottomBottomBound == yTopTopBound
    // |   top-1   |
    // |-----------| yCenterTopBound == yTopBottomBound
    // |  center-1 |
    // |-----------| yCenterBottomBound == yBottomTopBound
    // |  bottom-1 |
    // |           |
    // |           |
    // |-----------| yBottomScopeBound (if successor index == successors.length - 1)
    public static boolean isInsideBottomArea(FlowGraph graph, List<GraphNode> successors, int successorIndex, ScopedGraphNode closestPrecedingNode, Point dropPoint, Graphics2D graphics) {
        GraphNode current = successors.get(successorIndex);
        // Lowermost element bottom area: between yBottomTopBound and the bottom of the scope rectangle
        if (successorIndex == successors.size() - 1) {
            int yBottomScopeBound = scopeMaxYBound(graph, closestPrecedingNode, graphics);
            int yBottomTopBound = yBottomTopBoundOf(current, graphics);
            return dropPoint.y <= yBottomScopeBound && dropPoint.y > yBottomTopBound;
        } else {
            // Bottom area: between the following yTopBottomBound and current yBottomTopBound
            GraphNode following = successors.get(successorIndex + 1);
            int yBottomTopBound = yBottomTopBoundOf(current, graphics);
            int yTopBottomBound = yTopBottomBoundOf(following, graphics);
            return dropPoint.y > yTopBottomBound && dropPoint.y < yBottomTopBound;
        }
    }

    public static boolean isInsideCenterArea(GraphNode node, Point dropPoint, Graphics2D graphics) {
        int yCenterTopBound = yCenterTopBound(node, graphics);
        int yCenterBottomBound = yCenterBottomBound(node, graphics);
        return dropPoint.y >= yCenterTopBound && dropPoint.y <= yCenterBottomBound;
    }

    private static int yTopBottomBoundOf(GraphNode node, Graphics2D graphics) {
        int topHalfHeight = node.topHalfHeight(graphics);
        return (node.y() - topHalfHeight) + getByPercent(topHalfHeight, NODE_TOP_MARGIN_PERCENT);
    }

    private static int yBottomTopBoundOf(GraphNode node, Graphics2D graphics) {
        int bottomHalfHeight = node.bottomHalfHeight(graphics);
        return (node.y() + bottomHalfHeight) - getByPercent(bottomHalfHeight, NODE_BOTTOM_MARGIN_PERCENT);
    }

    private static int yCenterTopBound(GraphNode node, Graphics2D graphics) {
        return yTopBottomBoundOf(node, graphics);
    }

    private static int yCenterBottomBound(GraphNode node, Graphics2D graphics) {
        return yBottomTopBoundOf(node, graphics);
    }

    private static int scopeMaxYBound(FlowGraph graph, ScopedGraphNode scopedGraphNode, Graphics2D graphics) {
        ScopeBoundaries boundaries = scopedGraphNode.getScopeBoundaries(graph, graphics);
        return boundaries.getY() + boundaries.getHeight();
    }

    private static int scopeMinYBound(FlowGraph graph, ScopedGraphNode scopedGraphNode, Graphics2D graphics) {
        ScopeBoundaries boundaries = scopedGraphNode.getScopeBoundaries(graph, graphics);
        return boundaries.getY();
    }

    private static int getByPercent(int value, int percent) {
        return Math.round(((float) value / (float) 100) * percent);
    }
}
