package com.esb.plugin.graph.layout;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;

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

    List<List<GraphNode>> compute() {
        List<List<GraphNode>> sorted = new ArrayList<>();

        List<GraphNode> noIncomingEdgesDrawables = getNodesWithoutIncomingEdges();

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

    private List<GraphNode> getNodesWithoutIncomingEdges() {
        List<GraphNode> collect = graph
                .nodes()
                .stream()
                .filter(node -> graph.predecessors(node).isEmpty())
                .collect(Collectors.toList());
        Collections.reverse(collect);
        return collect;
    }

}
