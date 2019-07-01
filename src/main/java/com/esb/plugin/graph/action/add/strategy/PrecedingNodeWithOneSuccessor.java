package com.esb.plugin.graph.action.add.strategy;

import com.esb.plugin.commons.Half;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopeBoundaries;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.utils.BelongToSameScope;
import com.esb.plugin.graph.utils.FindScope;
import com.esb.plugin.graph.utils.FindScopes;
import com.esb.plugin.graph.utils.ListLastNodesOfScope;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.Stack;

import static com.google.common.base.Preconditions.checkState;

public class PrecedingNodeWithOneSuccessor implements Strategy {

    private final GraphNode precedingNode;
    private final Graphics2D graphics;
    private final FlowGraph graph;
    private final Point dropPoint;

    PrecedingNodeWithOneSuccessor(@NotNull FlowGraph graph, @NotNull Point dropPoint, @NotNull GraphNode precedingNode, @NotNull Graphics2D graphics) {
        this.precedingNode = precedingNode;
        this.dropPoint = dropPoint;
        this.graphics = graphics;
        this.graph = graph;
    }

    @Override
    public void execute(GraphNode node) {
        List<GraphNode> successors = graph.successors(precedingNode);
        checkState(successors.size() == 1,
                "Successors size MUST be 1, otherwise it would not be preceding node with one successor strategy");

        GraphNode successorOfClosestPrecedingNode = successors.get(0);

        if (BelongToSameScope.from(graph, precedingNode, successorOfClosestPrecedingNode)) {
            if (withinYBounds(dropPoint.y, precedingNode)) {
                graph.add(precedingNode, node);
                graph.add(node, successorOfClosestPrecedingNode);
                graph.remove(precedingNode, successorOfClosestPrecedingNode);

                // We add it to the scope
                FindScope.of(graph, precedingNode)
                        .ifPresent(scopedGraphNode ->
                                scopedGraphNode.addToScope(node));
            }
        } else {
            // They belong to different scopes
            handleDifferentScopes(precedingNode, node, successorOfClosestPrecedingNode);
        }
    }

    private void handleDifferentScopes(GraphNode closestPrecedingNode, GraphNode node, GraphNode successorOfClosestPrecedingNode) {

        Stack<ScopedGraphNode> scopes = FindScopes.of(graph, closestPrecedingNode);
        if (scopes.isEmpty()) {
            graph.add(closestPrecedingNode, node);
            graph.add(node, successorOfClosestPrecedingNode);
            graph.remove(closestPrecedingNode, successorOfClosestPrecedingNode);
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
            ListLastNodesOfScope
                    .from(graph, lastInnerMostScope)
                    .forEach(lastNodeOfScope -> {
                        graph.add(lastNodeOfScope, node);
                        graph.remove(lastNodeOfScope, successorOfClosestPrecedingNode);
                    });
        } else {
            graph.add(closestPrecedingNode, node);
        }

        graph.add(node, successorOfClosestPrecedingNode);
        graph.remove(closestPrecedingNode, successorOfClosestPrecedingNode);

        if (currentScope != null) {
            currentScope.addToScope(node);
        }
    }

    private boolean withinYBounds(int dropY, GraphNode node) {
        int halfHeight = Half.of(node.height(graphics));
        return dropY > node.y() - halfHeight &&
                dropY < node.y() + halfHeight;
    }

    private static int scopeMaxXBound(FlowGraph graph, ScopedGraphNode scopedGraphNode, Graphics2D graphics) {
        ScopeBoundaries scopeBoundaries = scopedGraphNode.getScopeBoundaries(graph, graphics);
        return scopeBoundaries.getX() + scopeBoundaries.getWidth();
    }
}
