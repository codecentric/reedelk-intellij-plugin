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

abstract class AbstractFindMaxHeight {

    private Measurer measurer;

    AbstractFindMaxHeight(Measurer measurer) {
        this.measurer = measurer;
    }

    interface Measurer {
        int measure(GraphNode node, Graphics2D graphics);
    }

    int _of(FlowGraph graph, Graphics2D graphics, GraphNode start, GraphNode firstNodeOutsideScope) {
        int currentMax = measurer.measure(start, graphics);

        java.util.List<GraphNode> nextNodes = singletonList(start);

        return _of(graph, graphics, nextNodes, firstNodeOutsideScope, currentMax);
    }

    private int _of(FlowGraph graph, Graphics2D graphics, java.util.List<GraphNode> nodes, GraphNode firstNodeOutsideScope, int currentMax) {
        int newMax = currentMax;

        for (GraphNode node : nodes) {

            // We stop if he node is the first
            // node outside the scope
            if (node == firstNodeOutsideScope) {
                return newMax;
            }

            List<GraphNode> nextNodes = new ArrayList<>();

            // A scoped  node is ALWAYS at  the center of its  successors. Therefore
            // we don't need to find the top/bottom half height, but we just split its
            // maximum height in  half.
            if (node instanceof ScopedGraphNode) {
                GraphNode firstNodeOutside =
                        FindFirstNodeOutsideScope.of(graph, (ScopedGraphNode) node).orElse(null);

                int scopeMaxHeight = ComputeMaxHeight.of(graph, graphics, node, firstNodeOutside);

                int halfScopeMaxHeight = Half.of(scopeMaxHeight);
                if (halfScopeMaxHeight > newMax) {
                    newMax = halfScopeMaxHeight;
                }

                if (firstNodeOutside != null) {
                    nextNodes.add(firstNodeOutside);
                }

            } else {

                //  If the current node has a bottom/top half height
                // taller than the current max, then replace the current max.
                int currentNodeMax = measurer.measure(node, graphics);
                if (currentNodeMax > newMax) {
                    newMax = currentNodeMax;
                }

                nextNodes.addAll(graph.successors(node));
            }

            newMax = _of(graph, graphics, nextNodes, firstNodeOutsideScope, newMax);
        }

        return newMax;
    }

}
