package com.esb.plugin.graph.utils;

import com.esb.plugin.commons.Half;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.layout.utils.ComputeMaxHeight;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;

import java.awt.*;
import java.util.Optional;

public class FindTopHalfHeight {

    public static int of(FlowGraph graph, Graphics2D graphics, GraphNode start, GraphNode firstNodeOutsideScope, int currentMax) {
        java.util.List<GraphNode> successors = graph.successors(start);
        int newMax = currentMax;
        for (GraphNode successor : successors) {
            if (successor == firstNodeOutsideScope) return newMax;
            if (successor instanceof ScopedGraphNode) {
                Optional<GraphNode> firstNodeOutside = FindFirstNodeOutsideScope.of(graph, (ScopedGraphNode) successor);
                int of = ComputeMaxHeight.of(graph, graphics, successor, firstNodeOutside.get());
                if (Half.of(of) > newMax) {
                    newMax = Half.of(of);
                }
                continue;
            }
            if (successor.topHalfHeight(graphics) > newMax) {
                newMax = successor.topHalfHeight(graphics);

            }
            newMax = of(graph, graphics, successor, firstNodeOutsideScope, newMax);
        }
        return newMax;
    }

}
