package com.reedelk.plugin.graph.utils;

import com.reedelk.plugin.component.type.stop.StopNode;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;

import java.util.Collection;

import static java.util.stream.Collectors.toList;

class GraphNodeFilters {

    private GraphNodeFilters() {
    }

    static class StopNodes {
        static Collection<StopNode> from(Collection<GraphNode> nodes) {
            return nodes.stream()
                    .filter(n -> n instanceof StopNode)
                    .map(n -> (StopNode) n)
                    .collect(toList());
        }
    }

    static class ScopedGraphNodes {
        static Collection<ScopedGraphNode> from(Collection<GraphNode> nodes) {
            return nodes.stream()
                    .filter(n -> n instanceof ScopedGraphNode)
                    .map(n -> (ScopedGraphNode) n)
                    .collect(toList());
        }
    }
}