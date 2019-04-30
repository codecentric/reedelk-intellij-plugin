package com.esb.plugin.graph.scope;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedNode;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class FindJoiningScope {

    public static Optional<ScopedNode> of(FlowGraph graph, GraphNode target) {
        List<ScopedNode> scopedNodes = ListAllScopes.of(graph);
        List<ScopedNode> scopesWithTargetAsFirstOutsideNode = scopedNodes.stream()
                .filter(scopedDrawable -> {
                    Optional<GraphNode> firstNode = FindFirstNodeOutsideScope.of(graph, scopedDrawable);
                    return firstNode.filter(firstNodeOutsideScope -> firstNodeOutsideScope == target).isPresent();
                }).collect(toList());

        return filterOutermostScope(scopesWithTargetAsFirstOutsideNode);
    }

    // We must to return the outermost amongst all the scopes.
    // The outermost is the one with the lowest X coordinate.
    private static Optional<ScopedNode> filterOutermostScope(List<ScopedNode> scopes) {
        ScopedNode outermost = null;
        for (ScopedNode scope : scopes) {
            if (outermost == null) {
                outermost = scope;
                continue;
            }
            if (scope.x() < outermost.x()) {
                outermost = scope;
            }
        }
        return Optional.ofNullable(outermost);
    }
}
