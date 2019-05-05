package com.esb.plugin.graph.utils;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class FindJoiningScope {

    public static Optional<ScopedGraphNode> of(FlowGraph graph, GraphNode target) {
        List<ScopedGraphNode> scopedGraphNodes = ListAllScopes.of(graph);
        List<ScopedGraphNode> scopesWithTargetAsFirstOutsideNode = scopedGraphNodes.stream()
                .filter(scopedDrawable -> {
                    Optional<GraphNode> firstNode = FindFirstNodeOutsideScope.of(graph, scopedDrawable);
                    return firstNode.filter(firstNodeOutsideScope -> firstNodeOutsideScope == target).isPresent();
                }).collect(toList());

        return filterOutermostScope(scopesWithTargetAsFirstOutsideNode);
    }

    // We must to return the outermost amongst all the scopes.
    // The outermost is the one with the lowest X coordinate.
    private static Optional<ScopedGraphNode> filterOutermostScope(List<ScopedGraphNode> scopes) {
        ScopedGraphNode outermost = null;
        for (ScopedGraphNode scope : scopes) {
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
