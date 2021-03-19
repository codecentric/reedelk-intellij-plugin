package de.codecentric.reedelk.plugin.graph.utils;

import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.node.ScopedGraphNode;
import de.codecentric.reedelk.plugin.commons.IsScopedGraphNode;
import de.codecentric.reedelk.plugin.component.type.stop.StopNode;

import java.util.Collection;

import static java.util.stream.Collectors.toList;

class GraphNodeFilters {

    private GraphNodeFilters() {
    }

    static class StopNodes {

        private StopNodes() {
        }

        static Collection<StopNode> from(Collection<GraphNode> nodes) {
            return nodes.stream()
                    .filter(n -> n instanceof StopNode)
                    .map(n -> (StopNode) n)
                    .collect(toList());
        }
    }

    static class ScopedGraphNodes {

        private ScopedGraphNodes() {
        }

        static Collection<ScopedGraphNode> from(Collection<GraphNode> nodes) {
            return nodes.stream()
                    .filter(IsScopedGraphNode::of)
                    .map(n -> (ScopedGraphNode) n)
                    .collect(toList());
        }
    }
}
