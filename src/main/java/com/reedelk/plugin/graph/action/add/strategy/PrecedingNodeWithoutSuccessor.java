package com.reedelk.plugin.graph.action.add.strategy;

import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.action.Strategy;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopeBoundaries;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.graph.utils.FindScopes;
import com.reedelk.plugin.graph.utils.ListLastNodesOfScope;
import org.jetbrains.annotations.NotNull;

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
public class PrecedingNodeWithoutSuccessor implements Strategy {

    private final GraphNode closestPrecedingNode;
    private final Graphics2D graphics;
    private final FlowGraph graph;
    private final Point dropPoint;

    PrecedingNodeWithoutSuccessor(FlowGraph graph, Point dropPoint, GraphNode closestPrecedingNode, Graphics2D graphics) {
        this.closestPrecedingNode = closestPrecedingNode;
        this.dropPoint = dropPoint;
        this.graphics = graphics;
        this.graph = graph;
    }

    @Override
    public void execute(GraphNode node) {

        Stack<ScopedGraphNode> scopes = FindScopes.of(graph, closestPrecedingNode);

        if (scopes.isEmpty()) {
            graph.add(closestPrecedingNode, node);
            return;
        }

        ScopedGraphNode currentScope = null;
        ScopedGraphNode lastInnerMostScope = null;

        while (!scopes.isEmpty()) {

            currentScope = scopes.pop();

            int maxXBound = scopeMaxXBound(graph, currentScope, graphics);

            if (dropPoint.x <= maxXBound) break;

            lastInnerMostScope = currentScope;

            if (scopes.isEmpty()) {
                currentScope = null;
            }
        }

        if (lastInnerMostScope != null) {
            ListLastNodesOfScope.from(graph, lastInnerMostScope)
                    .forEach(lastNodeOfScope -> graph.add(lastNodeOfScope, node));

        } else {
            graph.add(closestPrecedingNode, node);
        }

        if (currentScope != null) {
            currentScope.addToScope(node);
        }
    }

    private static int scopeMaxXBound(@NotNull FlowGraph graph, @NotNull ScopedGraphNode scopedGraphNode, @NotNull Graphics2D graphics) {
        ScopeBoundaries scopeBoundaries = scopedGraphNode.getScopeBoundaries(graph, graphics);
        return scopeBoundaries.getX() + scopeBoundaries.getWidth();
    }
}