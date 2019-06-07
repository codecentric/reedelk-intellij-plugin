package com.esb.plugin.graph.action.strategy;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.utils.FindFirstNodeOutsideScope;

import java.awt.*;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

/**
 * Strategy for preceding Scoped Node type.
 * Only a scoped node might have more than one successor.
 */
public class PrecedingScopedNode extends AbstractStrategy {

    public PrecedingScopedNode(FlowGraph graph, Point dropPoint, GraphNode node, Graphics2D graphics) {
        super(graph, dropPoint, node, graphics);
    }

    // It is the only type of node with potentially many successors.
    @Override
    public void execute(GraphNode scopeNode) {
        checkState(scopeNode instanceof ScopedGraphNode, "Strategy only accepts ScopedGraphNode");

        ScopedGraphNode closestPrecedingNode = (ScopedGraphNode) scopeNode;

        List<GraphNode> successors = graph.successors(closestPrecedingNode);

        if (successors.isEmpty()) {
            graph.add(closestPrecedingNode, node);
            addToScopeIfNeeded(closestPrecedingNode);
            return;
        }

        // Need to  handle the case where successor is only one and it is outside the scope.
        if (successors.size() == 1 && !closestPrecedingNode.scopeContains(successors.get(0))) {
            GraphNode successorOfClosestPrecedingNode = successors.get(0);
            graph.remove(closestPrecedingNode, successorOfClosestPrecedingNode);
            graph.add(closestPrecedingNode, node);
            graph.add(node, successorOfClosestPrecedingNode);
            addToScopeIfNeeded(closestPrecedingNode);
            return;
        }

        for (int successorIndex = 0; successorIndex < successors.size(); successorIndex++) {
            // |-----------| yTopTopBound
            // |    top    |
            // |-----------| yCenterTopBound == yTopBottomBound
            // |   center  |
            // |-----------| yCenterBottomBound == yBottomTopBound
            // |  bottom   |
            // |-----------| yBottomBottomBound
            GraphNode successor = successors.get(successorIndex);
            if (isInsideTopArea(successor, dropPoint)) {
                if (node.isSuccessorAllowed(graph, closestPrecedingNode, successorIndex)) {
                    graph.add(closestPrecedingNode, node, successorIndex);
                    closestPrecedingNode.addToScope(node);
                    FindFirstNodeOutsideScope.of(graph, closestPrecedingNode)
                            .ifPresent(firstNodeOutsideScope -> graph.add(node, firstNodeOutsideScope));
                }
                break;

            } else if (isInsideCenterArea(successor, dropPoint)) {
                // Replaces the first node at index "successorIndex"
                if (node.isSuccessorAllowed(graph, closestPrecedingNode, successorIndex)) {
                    graph.remove(closestPrecedingNode, successor);
                    graph.add(closestPrecedingNode, node, successorIndex);
                    graph.add(node, successor);
                    closestPrecedingNode.addToScope(node);
                }
                break;

            } else if (isInsideBottomArea(successor, dropPoint)) {
                // Replaces the first node at index "successorIndex"
                if (node.isSuccessorAllowed(graph, closestPrecedingNode, successorIndex + 1)) {
                    graph.add(closestPrecedingNode, node, successorIndex + 1);
                    closestPrecedingNode.addToScope(node);
                    FindFirstNodeOutsideScope.of(graph, closestPrecedingNode)
                            .ifPresent(firstNodeOutsideScope -> graph.add(node, firstNodeOutsideScope));
                }
                break;
            }
        }
    }

    private boolean isInsideTopArea(GraphNode node, Point dropPoint) {
        int height = node.height(graphics);
        int halfHeight = Math.floorDiv(height, 2);
        int yTopTopBound = node.y() - halfHeight;
        int yTopBottomBound = node.y() - (halfHeight - Math.floorDiv(height, 4));
        return dropPoint.y > yTopTopBound && dropPoint.y < yTopBottomBound;
    }

    private boolean isInsideCenterArea(GraphNode node, Point dropPoint) {
        int height = node.height(graphics);
        int halfHeight = Math.floorDiv(height, 2);
        int yCenterTopBound = node.y() - (halfHeight - Math.floorDiv(height, 4));
        int yCenterBottomBound = node.y() + (halfHeight - Math.floorDiv(height, 4));
        return dropPoint.y >= yCenterTopBound && dropPoint.y <= yCenterBottomBound;
    }

    private boolean isInsideBottomArea(GraphNode node, Point dropPoint) {
        int height = node.height(graphics);
        int halfHeight = Math.floorDiv(height, 2);
        int yBottomTopBound = node.y() + (halfHeight - Math.floorDiv(height, 4));
        int yBottomBottomBound = node.y() + halfHeight;
        return dropPoint.y > yBottomTopBound && dropPoint.y < yBottomBottomBound;
    }

}
