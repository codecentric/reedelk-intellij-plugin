package com.esb.plugin.graph.utils;

import com.esb.plugin.commons.Half;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.layout.utils.ComputeMaxHeight;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;

public class FindMaxBottomHalfHeight {

    public static int of(FlowGraph graph, Graphics2D graphics, GraphNode start, GraphNode firstNodeOutsideScope) {
        int currentMax = start.bottomHalfHeight(graphics);
        List<GraphNode> nextNodes = singletonList(start);
        return of(graph, graphics, nextNodes, firstNodeOutsideScope, currentMax);
    }

    private static int of(FlowGraph graph, Graphics2D graphics, List<GraphNode> nodes, GraphNode firstNodeOutsideScope, int currentMax) {
        int newMax = currentMax;

        for (GraphNode node : nodes) {

            // We stop if he node is the first
            // node outside the scope
            if (node == firstNodeOutsideScope) {
                return newMax;
            }

            List<GraphNode> nextNodes = new ArrayList<>();

            // A scoped  node is ALWAYS at  the center of its  successors. Therefore
            // we don't need to find the bottom half height, but we just split its
            // maximum height in  half.
            if (node instanceof ScopedGraphNode) {
                GraphNode firstNodeOutside =
                        FindFirstNodeOutsideScope.of(graph, (ScopedGraphNode) node).orElse(null);

                int scopeMaxHeight = ComputeMaxHeight.of(graph, graphics, node, firstNodeOutside);

                int halfScopeMaxHeight = Half.of(scopeMaxHeight);
                if (halfScopeMaxHeight > newMax) {
                    newMax = Half.of(scopeMaxHeight);
                }

                if (firstNodeOutside != null) {
                    nextNodes.add(firstNodeOutside);
                }

            } else {

                if (node.bottomHalfHeight(graphics) > newMax) {
                    newMax = node.bottomHalfHeight(graphics);
                }

                nextNodes.addAll(graph.successors(node));
            }

            newMax = of(graph, graphics, nextNodes, firstNodeOutsideScope, newMax);
        }

        return newMax;
    }
}
