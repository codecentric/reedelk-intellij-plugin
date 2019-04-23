package com.esb.plugin.designer.graph.scope;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

// TODO: Test this method
public class FindJoiningScope {

    public static Optional<ScopedDrawable> of(FlowGraph graph, Drawable drawable) {
        List<ScopedDrawable> scopedDrawables = ListAllScopes.of(graph);
        List<ScopedDrawable> scopes = scopedDrawables.stream()
                .filter(scopedDrawable -> {
                    Optional<Drawable> firstNode = FindFirstNodeOutsideScope.of(graph, scopedDrawable);
                    return firstNode.filter(drawable1 -> drawable1 == drawable).isPresent();
                }).collect(toList());

        // TODO: we need to select the outer most amongst all the scopes.
        // The outermost is the one with the lowest X
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
