package de.codecentric.reedelk.plugin.graph.utils;

import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.node.ScopedGraphNode;

/**
 * Checks whether the given target scope is the last scope right before the target node.
 * <p>
 * |---------------------------------|
 * |     |-------------              |
 * |     |    | -> N1 |              |
 * |     | S2 |       |  // no arrow |
 * |     |    | -> N2 |              |
 * |     |-------------              |
 * |  S1 |                           | -> N7
 * |     |-------------              |
 * |     |    | -> N3 |              |
 * |     | S3 |       | -> N5 -> N6  |
 * |     |    | -> N4 |              |
 * |     |-------------              |
 * |----------------------------------
 * <p>
 * Example where the given target scope IS NOT the last scope before the target node:
 * * targetScope = S2 and targetNode = N7
 * * targetScope = S3 and targetNode = N5
 * <p>
 * Example where the given target scope IS the last scope before the target node:
 * * targetScope = S1 and targetNode = N7
 */
public class IsLastScopeBeforeNode {

    private IsLastScopeBeforeNode() {
    }

    public static boolean of(FlowGraph graph, ScopedGraphNode targetScope, GraphNode targetNode) {
        return FindScope.of(graph, targetNode)
                .map(targetNodeScope -> targetNodeScope.scopeContains(targetScope))
                .orElseGet(() -> !FindScope.of(graph, targetScope).isPresent());
    }
}
