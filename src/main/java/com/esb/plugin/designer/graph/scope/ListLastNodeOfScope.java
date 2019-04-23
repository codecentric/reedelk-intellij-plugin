package com.esb.plugin.designer.graph.scope;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public class ListLastNodeOfScope {

    public static List<Drawable> from(FlowGraph graph, ScopedDrawable scopedDrawable) {
        Collection<Drawable> allDrawablesInScopeAndNestedScope = collectAllDrawablesInsideScopesFrom(scopedDrawable);
        return allDrawablesInScopeAndNestedScope.stream().filter(drawable -> {
            List<Drawable> successors = graph.successors(drawable);
            if (successors.isEmpty()) return true;
            // If exists at least one
            return !allDrawablesInScopeAndNestedScope.containsAll(successors);
        }).collect(toList());
    }


    private static Collection<Drawable> collectAllDrawablesInsideScopesFrom(ScopedDrawable scopedDrawable) {
        Collection<Drawable> scope = scopedDrawable.getScope();
        Set<Drawable> allElements = new HashSet<>(scope);
        Set<Drawable> nested = new HashSet<>();
        allElements.forEach(drawable -> {
            if (drawable instanceof ScopedDrawable) {
                nested.addAll(collectAllDrawablesInsideScopesFrom((ScopedDrawable) drawable));
            }
        });
        allElements.addAll(nested);
        allElements.add(scopedDrawable);
        return allElements;
    }
}
