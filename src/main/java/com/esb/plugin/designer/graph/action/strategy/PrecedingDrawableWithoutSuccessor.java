package com.esb.plugin.designer.graph.action.strategy;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.ScopeUtilities;
import com.esb.plugin.designer.graph.connector.Connector;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;

import java.awt.*;
import java.util.Stack;

public class PrecedingDrawableWithoutSuccessor extends AbstractAddStrategy {


    public PrecedingDrawableWithoutSuccessor(FlowGraph graph, Point dropPoint, Connector connector) {
        super(graph, dropPoint, connector);
    }

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
    @Override
    public void execute(Drawable closestPrecedingDrawable) {
        // Find all the scopes this closest preceding node belongs to.
        Stack<ScopedDrawable> stackOfScopes = ScopeUtilities.findTargetScopes(graph, closestPrecedingDrawable);

        if (stackOfScopes.isEmpty()) {
            connector.addPredecessor(closestPrecedingDrawable);
            return;
        }

        ScopedDrawable lastInnerMostScope = null;
        ScopedDrawable currentScope = null;
        while (!stackOfScopes.isEmpty()) {
            currentScope = stackOfScopes.pop();
            int maxXBound = ScopeUtilities.getMaxScopeXBound(graph, currentScope);
            if (dropPoint.x <= maxXBound) break;
            lastInnerMostScope = currentScope;
        }

        if (lastInnerMostScope != null) {
            ScopeUtilities
                    .listLastDrawablesOfScope(graph, lastInnerMostScope)
                    .forEach(connector::addPredecessor);
        } else {
            connector.addPredecessor(closestPrecedingDrawable);
        }

        connector.addToScope(currentScope);
    }
}
