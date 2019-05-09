package com.esb.plugin.graph.utils;

import com.esb.plugin.component.stop.StopNode;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;

import java.util.Collection;

import static java.util.stream.Collectors.toList;

public class GraphNodeFilters {

    public static class StopNodes {
        static Collection<StopNode> from(Collection<GraphNode> nodes) {
            return nodes.stream()
                    .filter(n -> n instanceof StopNode)
                    .map(n -> (StopNode) n)
                    .collect(toList());
        }
    }

    public static class ScopedGraphNodes {
        static Collection<ScopedGraphNode> from(Collection<GraphNode> nodes) {
            return nodes.stream()
                    .filter(n -> n instanceof ScopedGraphNode)
                    .map(n -> (ScopedGraphNode) n)
                    .collect(toList());
        }
    }

}
