package com.reedelk.plugin.graph.action.add.strategy;

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

    private static final int NODE_TOP_MARGIN_PERCENT = 35;
    private static final int NODE_BOTTOM_MARGIN_PERCENT = 40;

    private final ScopedGraphNode closestPrecedingNode;
    private final Graphics2D graphics;
    private final FlowGraph graph;
    private final Point dropPoint;

    PrecedingScopedNode(@NotNull FlowGraph graph, @NotNull Point dropPoint, @NotNull ScopedGraphNode scopedNode, @NotNull Graphics2D graphics) {
        this.closestPrecedingNode = scopedNode;
        this.dropPoint = dropPoint;
        this.graphics = graphics;
        this.graph = graph;
    }

    // We must find where the drop point is for each successor of preceding the scoped node.
    // The preceding scope node might have 'n' children, therefore for each children we need to find
    // the exact point where the  drop point is. For a given child, the drop point might be:
    // * In the topmost area: in this case the node must be added above the current child.
    // * In the center area: in this case the node must be added before the current child.
    // * In the bottom area: in this case the node must be added below the current child and above the next child.
    // Special cases are considered when the drop point is at the top of the topmost child
    // or at the bottom of the bottommost child.
    //
    // |-----------| yTopScopeBound (if successorIndex == 0)
    // |           |
    // |-----------| yTopTopBound
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
    // |-----------| yBottomBottomBound
    // |           |
    // |-----------| yBottomScopeBound (if successor index == successors.length - 1)
    @Override
    public void execute(GraphNode node) {
        List<GraphNode> successors = graph.successors(closestPrecedingNode);
        for (int successorIndex = 0; successorIndex < successors.size(); successorIndex++) {

            GraphNode successor = successors.get(successorIndex);

            // Adds a node at index "successorIndex", the existing nodes are shifted down.
            if (isInsideTopArea(successors, successorIndex, closestPrecedingNode, dropPoint)) {
                // Node -> successorIndex
                // The other ones are shifted down
                if (closestPrecedingNode.isSuccessorAllowedTop(graph, node, successorIndex)) {
                    graph.add(closestPrecedingNode, node, successorIndex);
                    closestPrecedingNode.addToScope(node);
                    FindFirstNodeOutsideScope.of(graph, closestPrecedingNode)
                            .ifPresent(firstNodeOutsideScope -> graph.add(node, firstNodeOutsideScope));

                    closestPrecedingNode.onSuccessorAdded(graph, node, successorIndex);
                }
                break; // we stop if we find an area matching the position.

                // Replaces the first node at index "successorIndex".
            } else if (isInsideCenterArea(successor, dropPoint)) {
                if (closestPrecedingNode.isSuccessorAllowedBefore(graph, node, successorIndex)) {
                    graph.remove(closestPrecedingNode, successor);
                    graph.add(closestPrecedingNode, node, successorIndex);
                    graph.add(node, successor);
                    closestPrecedingNode.addToScope(node);

                    closestPrecedingNode.onSuccessorAdded(graph, node, successorIndex);
                }
                break; // we stop if we find an area matching the position.

                // Adds a node next to the current index. Existing nodes at "successorIndex + 1" are shifted down.
            } else if (isInsideBottomArea(successors, successorIndex, closestPrecedingNode, dropPoint)) {
                if (closestPrecedingNode.isSuccessorAllowedBottom(graph, node, successorIndex + 1)) {
                    graph.add(closestPrecedingNode, node, successorIndex + 1);
                    closestPrecedingNode.addToScope(node);
                    FindFirstNodeOutsideScope.of(graph, closestPrecedingNode)
                            .ifPresent(firstNodeOutsideScope -> graph.add(node, firstNodeOutsideScope));

                    closestPrecedingNode.onSuccessorAdded(graph, node, successorIndex);
                }
                break; // we stop if we find an area matching the position.
            }
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
