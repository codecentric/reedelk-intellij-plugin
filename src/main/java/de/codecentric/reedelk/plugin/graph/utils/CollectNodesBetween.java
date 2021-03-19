package de.codecentric.reedelk.plugin.graph.utils;

import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.node.ScopedGraphNode;
import de.codecentric.reedelk.plugin.commons.IsScopedGraphNode;

import java.util.*;

public class CollectNodesBetween {

    private CollectNodesBetween() {
    }

    public static Collection<GraphNode> them(FlowGraph graph, GraphNode n1, GraphNode n2) {

        if (n1 == n2) {
            return Collections.emptyList();
        }

        Set<GraphNode> accumulator = new HashSet<>();
        List<GraphNode> successors = graph.successors(n1);

        for (GraphNode successor : successors) {

            if (successor != n2) {
                accumulator.add(successor);
            }

            if (IsScopedGraphNode.of(successor)) {
                FindFirstNodeOutsideScope.of(graph, (ScopedGraphNode) successor)
                        .ifPresent(node -> {
                            if (node != n2) accumulator.add(node);
                            accumulator.addAll(CollectNodesBetween.them(graph, node, n2));
                        });

            } else {
                accumulator.addAll(CollectNodesBetween.them(graph, successor, n2));
            }
        }
        return accumulator;
    }
}
