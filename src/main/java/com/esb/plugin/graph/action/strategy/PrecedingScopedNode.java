package com.esb.plugin.graph.action.strategy;

import com.esb.plugin.editor.Tile;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.connector.Connector;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.utils.FindFirstNodeOutsideScope;

import java.awt.*;
import java.util.List;
import java.util.Optional;

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
    public void execute(GraphNode input) {
        checkState(input instanceof ScopedGraphNode, "Strategy only accepts ScopedGraphNode");

        ScopedGraphNode closestPrecedingNode = (ScopedGraphNode) input;

        List<GraphNode> successors = graph.successors(closestPrecedingNode);

        Optional<GraphNode> firstNodeOutsideScope = FindFirstNodeOutsideScope.of(graph, closestPrecedingNode);

        // If isEmpty(successors) or (size(successors) == 1 & successor(closestPrecedingNode) is NodeOutsideScope
        if (successors.isEmpty()) {
            connector.addPredecessor(closestPrecedingNode);
            addToScopeIfNeeded(closestPrecedingNode);
            return;
        } else {

            if (firstNodeOutsideScope.isPresent()) {
                GraphNode firstNodeOutside = firstNodeOutsideScope.get();
                if (successors.size() == 1 && successors.get(0) == firstNodeOutside) {
                    graph.remove(closestPrecedingNode, firstNodeOutside);
                    connector.addPredecessor(closestPrecedingNode);
                    connector.addSuccessor(firstNodeOutside);
                    addToScopeIfNeeded(closestPrecedingNode);
                    return;
                }
            }
        }

        for (int successorIndex = 0; successorIndex < successors.size(); successorIndex++) {
            // We search where it lies. if it is in the top
            //
            // /-----------\ yTopBound
            // |    TOP    |
            // |-----------| yBottomBound
            // |   CENTER  |
            // |-----------|
            // |  BOTTOM   |
            // \-----------/=
            // We search the node on which the drop point lies on the Y axis.
            // If one is found, we stop.
            GraphNode successor = successors.get(successorIndex);
            int yTopBound = successor.y() - (Tile.HALF_HEIGHT + Math.floorDiv(Tile.HEIGHT, 4));
            int yBottomBound = successor.y() - (Tile.HALF_HEIGHT - Math.floorDiv(Tile.HEIGHT, 4));

            if (dropPoint.y > yBottomBound && dropPoint.y < yTopBound) {
                connector.addPredecessor(closestPrecedingNode, successorIndex);
                addToScopeIfNeeded(closestPrecedingNode);
                connectCommonSuccessorsOf(closestPrecedingNode);
                return;
            }

            int yCenterTopBound = yBottomBound;
            int yCenterBottomBound = successor.y() + (Tile.HALF_HEIGHT - Math.floorDiv(Tile.HEIGHT, 4));
            if (dropPoint.y <= yCenterBottomBound && yCenterTopBound >= yCenterTopBound) {
                graph.remove(closestPrecedingNode, successor);
                connector.addPredecessor(closestPrecedingNode, successorIndex);
                connector.addSuccessor(successor);
                addToScopeIfNeeded(closestPrecedingNode);
                return;
            }
        }

        // This is in the last index, the first node , the start of a new row (route)
        connector.addPredecessor(closestPrecedingNode);
        addToScopeIfNeeded(closestPrecedingNode);
        connectCommonSuccessorsOf(closestPrecedingNode);
    }

    private void connectCommonSuccessorsOf(ScopedGraphNode closestPrecedingNode) {
        FindFirstNodeOutsideScope.of(graph, closestPrecedingNode)
                .ifPresent(connector::addSuccessor);
    }

}
