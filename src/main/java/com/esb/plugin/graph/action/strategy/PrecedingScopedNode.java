package com.esb.plugin.graph.action.strategy;

import com.esb.plugin.editor.Tile;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.connector.Connector;
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
public class PrecedingScopedNode extends AbstractAddStrategy {

    public PrecedingScopedNode(FlowGraph graph, Point dropPoint, Connector connector, Graphics2D graphics) {
        super(graph, dropPoint, connector, graphics);
    }

    // It is the only type of node with potentially many successors.
    @Override
    public void execute(GraphNode node) {
        checkState(node instanceof ScopedGraphNode, "Strategy only accepts ScopedGraphNode");

        ScopedGraphNode closestPrecedingNode = (ScopedGraphNode) node;

        List<GraphNode> successors = graph.successors(closestPrecedingNode);

        if (successors.isEmpty()) {
            connector.addPredecessor(closestPrecedingNode);
            addToScopeIfNeeded(closestPrecedingNode);
            return;
        }

        // Need to  handle the case where successor is only one and it is outside the scope.
        if (successors.size() == 1 && !closestPrecedingNode.scopeContains(successors.get(0))) {
            GraphNode successorOfClosestPrecedingNode = successors.get(0);
            graph.remove(closestPrecedingNode, successorOfClosestPrecedingNode);
            connector.addPredecessor(closestPrecedingNode);
            connector.addSuccessor(successorOfClosestPrecedingNode);
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
                connector.addPredecessor(closestPrecedingNode, successorIndex);
                addToScopeIfNeeded(closestPrecedingNode);
                connectCommonSuccessorsOf(closestPrecedingNode);
                return;

            } else if (isInsideCenterArea(successor, dropPoint)) {
                // Replaces the first node at index "successorIndex"
                graph.remove(closestPrecedingNode, successor);
                connector.addPredecessor(closestPrecedingNode, successorIndex);
                connector.addSuccessor(successor);
                addToScopeIfNeeded(closestPrecedingNode);
                return;

            } else if (isInsideBottomArea(successor, dropPoint)) {
                connector.addPredecessor(closestPrecedingNode, successorIndex + 1);
                addToScopeIfNeeded(closestPrecedingNode);
                connectCommonSuccessorsOf(closestPrecedingNode);
                return;
            }
        }
    }

    private boolean isInsideTopArea(GraphNode successor, Point dropPoint) {
        int yTopTopBound = successor.y() - Tile.HALF_HEIGHT;
        int yTopBottomBound = successor.y() - (Tile.HALF_HEIGHT - Math.floorDiv(Tile.HEIGHT, 4));
        return dropPoint.y > yTopTopBound && dropPoint.y < yTopBottomBound;
    }

    private boolean isInsideCenterArea(GraphNode successor, Point dropPoint) {
        int yCenterTopBound = successor.y() - (Tile.HALF_HEIGHT - Math.floorDiv(Tile.HEIGHT, 4));
        int yCenterBottomBound = successor.y() + (Tile.HALF_HEIGHT - Math.floorDiv(Tile.HEIGHT, 4));
        return dropPoint.y >= yCenterTopBound && dropPoint.y <= yCenterBottomBound;
    }

    private boolean isInsideBottomArea(GraphNode successor, Point dropPoint) {
        int yBottomTopBound = successor.y() + (Tile.HALF_HEIGHT - Math.floorDiv(Tile.HEIGHT, 4));
        int yBottomBottomBound = successor.y() + Tile.HALF_HEIGHT;
        return dropPoint.y > yBottomTopBound && dropPoint.y < yBottomBottomBound;
    }

    private void connectCommonSuccessorsOf(ScopedGraphNode closestPrecedingNode) {
        FindFirstNodeOutsideScope.of(graph, closestPrecedingNode)
                .ifPresent(connector::addSuccessor);
    }

}
