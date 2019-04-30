package com.esb.plugin.graph.action.strategy;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.connector.Connector;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedNode;
import com.esb.plugin.graph.scope.FindScopes;
import com.esb.plugin.graph.scope.ListLastNodeOfScope;

import java.awt.*;
import java.util.Stack;

/**
 * Handles the case where the closest preceding node is the LAST NODE.
 * If the closest preceding node belongs to a scope, then:
 * - if it is within the scope x edge, then add it to that scope.
 * - if it is NOT within the scope x edge, then finds in which scope it
 * belongs to and connect it from the last elements of the last innermost scope.
 * <p>
 * If the closest preceding node does not belong to a scope, then:
 * - Add the node as successor.
 */
public class PrecedingDrawableWithoutSuccessor extends AbstractAddStrategy {


    public PrecedingDrawableWithoutSuccessor(FlowGraph graph, Point dropPoint, Connector connector, Graphics2D graphics) {
        super(graph, dropPoint, connector, graphics);
    }

    @Override
    public void execute(GraphNode closestPrecedingDrawable) {

        Stack<ScopedNode> scopes = FindScopes.of(graph, closestPrecedingDrawable);

        if (scopes.isEmpty()) {
            connector.addPredecessor(closestPrecedingDrawable);
            return;
        }

        ScopedNode currentScope = null;
        ScopedNode lastInnerMostScope = null;

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
                    .forEach(connector::addPredecessor);

        } else {
            connector.addPredecessor(closestPrecedingDrawable);
        }

        if (currentScope != null) {
            connector.addToScope(currentScope);
        }
    }
}