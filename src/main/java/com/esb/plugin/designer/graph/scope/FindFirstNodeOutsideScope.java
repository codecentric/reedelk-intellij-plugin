package com.esb.plugin.designer.graph.scope;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;

import java.util.*;

import static com.google.common.base.Preconditions.checkState;

/**
 * It finds the first node outside the given ScopedDrawable.
 * By definition a scope block must be followed only by one node.
 */
public class FindFirstNodeOutsideScope {

    public static Optional<Drawable> of(FlowGraph graph, ScopedDrawable scope) {
        Collection<Drawable> lastDrawablesOfScope = ListLastNodeOfScope.from(graph, scope);
        Set<Drawable> firstDrawablesOutsideScope = new HashSet<>();
        lastDrawablesOfScope.forEach(lastDrawableOfScope -> {
            List<Drawable> successors = graph.successors(lastDrawableOfScope);
            firstDrawablesOutsideScope.addAll(successors);
        });
        checkState(firstDrawablesOutsideScope.isEmpty() ||
                        firstDrawablesOutsideScope.size() == 1,
                "First node outside scope must be asent or at most one");
        return firstDrawablesOutsideScope.stream().findFirst();
    }
}
