package com.esb.plugin.designer.graph.dnd;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

class ScopeUtilities {

    static boolean haveSameScope(FlowGraph graph, Drawable drawable1, Drawable drawable2) {
        Optional<ScopedDrawable> scope1 = findScope(graph, drawable1);
        Optional<ScopedDrawable> scope2 = findScope(graph, drawable2);
        if (!scope1.isPresent() && !scope2.isPresent()) {
            return true;  // no scope at all so it is the same overall scope.
        }
        if (scope1.isPresent()) {
            if (scope2.isPresent()) {
                return scope1.get() == scope2.get();
            }
        }
        return false;
    }


    static Optional<ScopedDrawable> findScope(FlowGraph graph, Drawable target) {
        return graph.nodes()
                .stream()
                .filter(drawable -> drawable instanceof ScopedDrawable)
                .map(drawable -> (ScopedDrawable) drawable)
                .filter(scopedDrawable -> scopedDrawable.contains(target))
                .findFirst();
    }

    // A node might belong to multiple scopes....
    static List<ScopedDrawable> findScopesForNode(FlowGraph graph, Drawable targetDrawable) {
        List<ScopedDrawable> scopedDrawables = new ArrayList<>();
        Collection<Drawable> nodes = graph.nodes();
        for (Drawable node : nodes) {
            if (node instanceof ScopedDrawable) {
                if (((ScopedDrawable) node).getDrawablesInScope().contains(targetDrawable)) {
                    // But this one might be part of another scope as well...
                    scopedDrawables.add((ScopedDrawable) node);
                    List<Drawable> predecessors = graph.predecessors(node);
                    if (!predecessors.isEmpty()) {
                        for (Drawable predecessor : predecessors) {
                            scopedDrawables.addAll(findScopesForNode(graph, predecessor));
                        }

                    }
                }
            }
        }
        return scopedDrawables;
    }

    static Collection<Drawable> findNodesConnectedToZeroOrOutsideScopeDrawables(FlowGraph graph, ScopedDrawable scope) {
        Collection<Drawable> drawablesInTheScope = scope.getDrawablesInScope();
        return drawablesInTheScope.stream().filter(drawable -> {
            List<Drawable> successors = graph.successors(drawable);
            if (successors.isEmpty()) return true;
            return !drawablesInTheScope.containsAll(successors);
        }).collect(toList());
    }

    static void addToScopeIfNecessary(FlowGraph graph, Drawable adjacentNode, Drawable targetDrawableToAdd) {
        if (adjacentNode instanceof ScopedDrawable) {
            ScopedDrawable scopedDrawable = (ScopedDrawable) adjacentNode;
            scopedDrawable.add(targetDrawableToAdd);
        }
        List<ScopedDrawable> scopedDrawableObjects = ScopeUtilities.findScopesForNode(graph, adjacentNode);
        scopedDrawableObjects.forEach(scopedDrawable -> scopedDrawable.add(targetDrawableToAdd));
    }

    static int getScopeXEdge(ScopedDrawable scopedDrawable) {
        return scopedDrawable.getDrawablesInScope().stream().mapToInt(Drawable::x).max().getAsInt() + Tile.HALF_WIDTH;
    }

    /**
     * It finds the first node outside the given ScopedDrawable.
     */
    static Optional<Drawable> findFirstNodeOutsideScope(FlowGraph graph, ScopedDrawable scopedDrawable) {
        return findFirstOutsideCollection(graph, scopedDrawable.getDrawablesInScope(), scopedDrawable);
    }

    private static Optional<Drawable> findFirstOutsideCollection(FlowGraph graph, Collection<Drawable> collection, Drawable root) {
        for (Drawable successor : graph.successors(root)) {
            if (!collection.contains(successor)) {
                return Optional.of(successor);
            }
            Optional<Drawable> found = findFirstOutsideCollection(graph, collection, successor);
            if (found.isPresent()) return found;
        }
        return Optional.empty();
    }
}
