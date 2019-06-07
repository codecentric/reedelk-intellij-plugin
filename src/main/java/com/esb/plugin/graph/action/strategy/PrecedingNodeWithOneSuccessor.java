package com.esb.plugin.graph.action.strategy;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.utils.BelongToSameScope;
import com.esb.plugin.graph.utils.FindScope;
import com.esb.plugin.graph.utils.FindScopes;
import com.esb.plugin.graph.utils.ListLastNodesOfScope;

import java.awt.*;
import java.util.List;
import java.util.Stack;

import static com.google.common.base.Preconditions.checkState;

public class PrecedingNodeWithOneSuccessor extends AbstractStrategy {

    public PrecedingNodeWithOneSuccessor(FlowGraph graph, Point dropPoint, GraphNode node, Graphics2D graphics) {
        super(graph, dropPoint, node, graphics);
    }

    @Override
    public void execute(GraphNode closestPrecedingNode) {
        List<GraphNode> successors = graph.successors(closestPrecedingNode);
        checkState(successors.size() == 1,
                "Successors size MUST be 1, otherwise it is a Scoped Drawable");

        GraphNode successorOfClosestPrecedingNode = successors.get(0);

        if (BelongToSameScope.from(graph, closestPrecedingNode, successorOfClosestPrecedingNode)) {
            if (withinYBounds(dropPoint.y, closestPrecedingNode)) {
                graph.add(closestPrecedingNode, node);
                graph.add(node, successorOfClosestPrecedingNode);
                graph.remove(closestPrecedingNode, successorOfClosestPrecedingNode);

                // We add it to the scope
                FindScope.of(graph, closestPrecedingNode)
                        .ifPresent(scopedGraphNode ->
                                scopedGraphNode.addToScope(node));
            }
        } else {
            // They belong to different scopes
            handleDifferentScopes(closestPrecedingNode, successorOfClosestPrecedingNode);
        }
    }

    private void handleDifferentScopes(GraphNode closestPrecedingDrawable, GraphNode successorOfClosestPrecedingNode) {

        Stack<ScopedGraphNode> scopes = FindScopes.of(graph, closestPrecedingDrawable);
        if (scopes.isEmpty()) {
            graph.add(closestPrecedingDrawable, node);
            graph.add(node, successorOfClosestPrecedingNode);
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
            ListLastNodesOfScope
                    .from(graph, lastInnerMostScope)
                    .forEach(lastNodeOfScope -> {
                        graph.add(lastNodeOfScope, node);
                        graph.remove(lastNodeOfScope, successorOfClosestPrecedingNode);
                    });
        } else {
            graph.add(closestPrecedingDrawable, node);
        }

        graph.add(node, successorOfClosestPrecedingNode);
        graph.remove(closestPrecedingDrawable, successorOfClosestPrecedingNode);

        if (currentScope != null) {
            currentScope.addToScope(node);
        }
    }

    private boolean withinYBounds(int dropY, GraphNode node) {
        int halfHeight = Math.floorDiv(node.height(graphics), 2);
        return dropY > node.y() - halfHeight &&
                dropY < node.y() + halfHeight;
    }
}
