package com.esb.plugin.designer.graph.scope;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.GraphNode;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class FindJoiningScope {

    public static Optional<ScopedDrawable> of(FlowGraph graph, GraphNode target) {
        List<ScopedDrawable> scopedDrawables = ListAllScopes.of(graph);
        List<ScopedDrawable> scopesWithTargetAsFirstOutsideNode = scopedDrawables.stream()
                .filter(scopedDrawable -> {
                    Optional<GraphNode> firstNode = FindFirstNodeOutsideScope.of(graph, scopedDrawable);
                    return firstNode.filter(firstNodeOutsideScope -> firstNodeOutsideScope == target).isPresent();
                }).collect(toList());

        return filterOutermostScope(scopesWithTargetAsFirstOutsideNode);
    }

    // We must to return the outermost amongst all the scopes.
    // The outermost is the one with the lowest X coordinate.
    private static Optional<ScopedDrawable> filterOutermostScope(List<ScopedDrawable> scopes) {
        ScopedDrawable outermost = null;
        for (ScopedDrawable scope : scopes) {
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
