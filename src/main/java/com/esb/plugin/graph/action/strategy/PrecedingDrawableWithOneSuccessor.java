package com.esb.plugin.graph.action.strategy;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.connector.Connector;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.scope.BelongToSameScope;
import com.esb.plugin.graph.scope.FindScopes;
import com.esb.plugin.graph.scope.ListLastNodeOfScope;

import java.awt.*;
import java.util.List;
import java.util.Stack;

import static com.google.common.base.Preconditions.checkState;

public class PrecedingDrawableWithOneSuccessor extends AbstractAddStrategy {


    public PrecedingDrawableWithOneSuccessor(FlowGraph graph, Point dropPoint, Connector connector, Graphics2D graphics) {
        super(graph, dropPoint, connector, graphics);
    }

    @Override
    public void execute(GraphNode closestPrecedingDrawable) {
        List<GraphNode> successors = graph.successors(closestPrecedingDrawable);
        checkState(successors.size() == 1, "Successors size MUST be 1, otherwise it is a Scoped Drawable");

        GraphNode successorOfClosestPrecedingNode = successors.get(0);

        if (BelongToSameScope.from(graph, closestPrecedingDrawable, successorOfClosestPrecedingNode)) {
            if (withinYBounds(dropPoint.y, closestPrecedingDrawable)) {
                connector.addPredecessor(closestPrecedingDrawable);
                connector.addSuccessor(successorOfClosestPrecedingNode);
                graph.remove(closestPrecedingDrawable, successorOfClosestPrecedingNode);
                addToScopeIfNeeded(closestPrecedingDrawable);
            }
        } else {
            // They belong to different scopes
            handleDifferentScopes(closestPrecedingDrawable, successorOfClosestPrecedingNode);
        }
    }

    private void handleDifferentScopes(GraphNode closestPrecedingDrawable, GraphNode successorOfClosestPrecedingNode) {

        Stack<ScopedGraphNode> scopes = FindScopes.of(graph, closestPrecedingDrawable);
        if (scopes.isEmpty()) {
            connector.addPredecessor(closestPrecedingDrawable);
            connector.addSuccessor(successorOfClosestPrecedingNode);
            graph.remove(closestPrecedingDrawable, successorOfClosestPrecedingNode);
            return;
        }

        ScopedGraphNode currentScope = null;
        ScopedGraphNode lastInnerMostScope = null;

        while (!scopes.isEmpty()) {

            currentScope = scopes.pop();

            int maxXBound = getScopeMaxXBound(graph, currentScope, graphics);

            if (dropPoint.x <= maxXBound) break;

            lastInnerMostScope = currentScope;

            if (scopes.isEmpty()) {
                currentScope = null;
            }
        }

        if (lastInnerMostScope != null) {
            ListLastNodeOfScope.from(graph, lastInnerMostScope)
                    .forEach(drawable -> {
                        connector.addPredecessor(drawable);
                        graph.remove(drawable, successorOfClosestPrecedingNode);
                    });
        } else {
            connector.addPredecessor(closestPrecedingDrawable);
        }

        connector.addSuccessor(successorOfClosestPrecedingNode);
        graph.remove(closestPrecedingDrawable, successorOfClosestPrecedingNode);

        if (currentScope != null) {
            connector.addToScope(currentScope);
        }

    }

    private boolean withinYBounds(int dropY, GraphNode node) {
        return dropY > node.y() - Tile.HALF_HEIGHT &&
                dropY < node.y() + Tile.HALF_HEIGHT;
    }
}
