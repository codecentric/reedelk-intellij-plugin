package com.esb.plugin.graph.utils;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollectNodesBetween {

    public static Collection<GraphNode> them(FlowGraph graph, GraphNode n1, GraphNode n2) {
        Set<GraphNode> accumulator = new HashSet<>();
        List<GraphNode> successors = graph.successors(n1);
        for (GraphNode successor : successors) {
            if (successor != n2) {
                accumulator.add(successor);
            }
            accumulator.addAll(CollectNodesBetween.them(graph, successor, n2));
        }
        return accumulator;
    }
}
