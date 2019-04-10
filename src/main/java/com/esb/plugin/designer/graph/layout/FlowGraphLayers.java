package com.esb.plugin.designer.graph.layout;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

// Layers:
// [0] ["N1"]
// [1] ["N2", "N3"]
// [2] ["N6", "N4", "N5"]
// [3] ["N7"]
class FlowGraphLayers {

    private final FlowGraph graph;

    FlowGraphLayers(FlowGraph graph) {
        this.graph = graph.copy();
    }

    List<List<Drawable>> compute() {
        List<List<Drawable>> sorted = new ArrayList<>();

        List<Drawable> noIncomingEdgesDrawables = getNodesWithoutIncomingEdges();

        while (!noIncomingEdgesDrawables.isEmpty()) {
            sorted.add(noIncomingEdgesDrawables);

            // Remove all edges starting from nodes in noIncomingEdgesNodes
            noIncomingEdgesDrawables.forEach(graph::removeEdgesStartingFrom);

            // Remove all nodes without incoming edge
            noIncomingEdgesDrawables.forEach(graph::remove);

            // Recompute nodes without incoming edges
            noIncomingEdgesDrawables = getNodesWithoutIncomingEdges();
        }

        return sorted;
    }

    private List<Drawable> getNodesWithoutIncomingEdges() {
        List<Drawable> collect = graph
                .nodes()
                .stream()
                .filter(node -> graph.predecessors(node).isEmpty())
                .collect(Collectors.toList());
        Collections.reverse(collect);
        return collect;
    }

}
