package com.reedelk.plugin.graph.action.add.strategy;

import com.reedelk.plugin.commons.Half;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.action.Strategy;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopeBoundaries;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.graph.utils.FindFirstNodeOutsideScope;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

/**
 * Strategy which can be applied to preceding nodes of type ScopedGraphNode.
 * Note that only a scoped node might have more than one successor.
 */
public class PrecedingScopedNode implements Strategy {

    private static final int NODE_TOP_BOTTOM_WEIGHT = 4;

    private final ScopedGraphNode closestPrecedingNode;
    private final Graphics2D graphics;
    private final FlowGraph graph;
    private final Point dropPoint;

    public PrecedingScopedNode(@NotNull FlowGraph graph, @NotNull Point dropPoint, @NotNull ScopedGraphNode scopedNode, @NotNull Graphics2D graphics) {
        this.closestPrecedingNode = scopedNode;
        this.dropPoint = dropPoint;
        this.graphics = graphics;
        this.graph = graph;
    }

    @Override
    public void execute(GraphNode node) {
        List<GraphNode> successors = graph.successors(closestPrecedingNode);
        for (int successorIndex = 0; successorIndex < successors.size(); successorIndex++) {
            // |-----------| yTopTopBound
            // |    top    |
            // |-----------| yCenterTopBound == yTopBottomBound
            // |   center  |
            // |-----------| yCenterBottomBound == yBottomTopBound
            // |  bottom   |
            // |-----------| yBottomBottomBound
            GraphNode successor = successors.get(successorIndex);

            // Adds a node at index "successorIndex", the existing nodes are shifted down.
            if (isInsideTopArea(successors, successorIndex, closestPrecedingNode, dropPoint)) {
                if (closestPrecedingNode.isSuccessorAllowed(graph, node, successorIndex)) {
                    graph.add(closestPrecedingNode, node, successorIndex);
                    closestPrecedingNode.addToScope(node);
                    FindFirstNodeOutsideScope.of(graph, closestPrecedingNode)
                            .ifPresent(firstNodeOutsideScope -> graph.add(node, firstNodeOutsideScope));
                }
                break;

                // Replaces the first node at index "successorIndex".
            } else if (isInsideCenterArea(successor, dropPoint)) {
                if (closestPrecedingNode.isSuccessorAllowed(graph, closestPrecedingNode, successorIndex)) {
                    graph.remove(closestPrecedingNode, successor);
                    graph.add(closestPrecedingNode, node, successorIndex);
                    graph.add(node, successor);
                    closestPrecedingNode.addToScope(node);
                }
                break;

                // Adds a node next to the current index. Existing nodes at "successorIndex + 1" are shifted down.
            } else if (isInsideBottomArea(successors, successorIndex, closestPrecedingNode, dropPoint)) {
                if (closestPrecedingNode.isSuccessorAllowed(graph, node, successorIndex + 1)) {
                    graph.add(closestPrecedingNode, node, successorIndex + 1);
                    closestPrecedingNode.addToScope(node);
                    FindFirstNodeOutsideScope.of(graph, closestPrecedingNode)
                            .ifPresent(firstNodeOutsideScope -> graph.add(node, firstNodeOutsideScope));
                }
                break;
            }
        }
    }

    // |-----------| yTopScopeBound
    // |           |
    // |           |
    // |-----------| yTopTopBound
    // |    top    |
    // |-----------| yCenterTopBound == yTopBottomBound
    // |   center  |
    private boolean isInsideTopArea(List<GraphNode> successors, int successorIndex, ScopedGraphNode closestPrecedingNode, Point dropPoint) {
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

    // |   center  |
    // |-----------| yCenterBottomBound == yBottomTopBound
    // |  bottom   |
    // |-----------| yBottomBottomBound
    // |           |
    // |           |
    // |-----------| yBottomScopeBound
    private boolean isInsideBottomArea(List<GraphNode> successors, int successorIndex, ScopedGraphNode closestPrecedingNode, Point dropPoint) {
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

    private boolean isInsideCenterArea(GraphNode node, Point dropPoint) {
        int yCenterTopBound = yCenterTopBound(node, graphics);
        int yCenterBottomBound = yCenterBottomBound(node, graphics);
        return dropPoint.y >= yCenterTopBound && dropPoint.y <= yCenterBottomBound;
    }

    private static int yTopBottomBoundOf(GraphNode node, Graphics2D graphics) {
        int height = node.height(graphics);
        int halfHeight = Half.of(height);
        return node.y() - (halfHeight - Math.floorDiv(height, NODE_TOP_BOTTOM_WEIGHT));
    }

    private static int yBottomTopBoundOf(GraphNode node, Graphics2D graphics) {
        int height = node.height(graphics);
        int halfHeight = Half.of(height);
        return node.y() + (halfHeight - Math.floorDiv(height, NODE_TOP_BOTTOM_WEIGHT));
    }

    private static int yCenterTopBound(GraphNode node, Graphics2D graphics) {
        int height = node.height(graphics);
        int halfHeight = Half.of(height);
        return node.y() - (halfHeight - Math.floorDiv(height, NODE_TOP_BOTTOM_WEIGHT));
    }

    private static int yCenterBottomBound(GraphNode node, Graphics2D graphics) {
        int height = node.height(graphics);
        int halfHeight = Half.of(height);
        return node.y() + (halfHeight - Math.floorDiv(height, NODE_TOP_BOTTOM_WEIGHT));
    }

    private static int scopeMaxYBound(FlowGraph graph, ScopedGraphNode scopedGraphNode, Graphics2D graphics) {
        ScopeBoundaries boundaries = scopedGraphNode.getScopeBoundaries(graph, graphics);
        return boundaries.getY() + boundaries.getHeight();
    }

    private static int scopeMinYBound(FlowGraph graph, ScopedGraphNode scopedGraphNode, Graphics2D graphics) {
        ScopeBoundaries boundaries = scopedGraphNode.getScopeBoundaries(graph, graphics);
        return boundaries.getY();
    }
}
