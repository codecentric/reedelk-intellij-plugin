package com.esb.plugin.designer.graph.layout;

import com.esb.plugin.designer.graph.DirectedGraph;
import com.esb.plugin.designer.graph.Node;

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

    private final DirectedGraph<Node> graph;

    FlowGraphLayers(DirectedGraph<Node> graph) {
        this.graph = graph.copy();
    }

    List<List<Node>> compute() {
        List<List<Node>> sorted = new ArrayList<>();

        List<Node> noIncomingEdgesNodes = getNodesWithoutIncomingEdges();

        while (!noIncomingEdgesNodes.isEmpty()) {
            sorted.add(noIncomingEdgesNodes);

            // Remove all edges starting from nodes in noIncomingEdgesNodes
            noIncomingEdgesNodes.forEach(graph::removeEdgesStartingFrom);

            // Remove all nodes without incoming edge
            noIncomingEdgesNodes.forEach(graph::removeNode);

            // Recompute nodes without incoming edges
            noIncomingEdgesNodes = getNodesWithoutIncomingEdges();
        }

        return sorted;
    }

    private List<Node> getNodesWithoutIncomingEdges() {
        List<Node> collect = graph
                .nodes()
                .stream()
                .filter(node -> graph.predecessors(node).isEmpty())
                .collect(Collectors.toList());
        Collections.reverse(collect);
        return collect;
    }

}
