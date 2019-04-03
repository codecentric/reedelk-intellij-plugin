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

public class ScopeUtilities {

    public static boolean belongToDifferentScopes(FlowGraph graph, Drawable drawable1, Drawable drawable2) {
        List<ScopedDrawable> scopesForNode1 = findScopesForNode(graph, drawable1);
        List<ScopedDrawable> scopesForNode2 = findScopesForNode(graph, drawable2);
        return !scopesForNode1.containsAll(scopesForNode2);
    }

    // A node might belong to multiple scopes....
    public static List<ScopedDrawable> findScopesForNode(FlowGraph graph, Drawable targetDrawable) {
        List<ScopedDrawable> scopedDrawables = new ArrayList<>();
        Collection<Drawable> nodes = graph.nodes();
        for (Drawable node : nodes) {
            if (node instanceof ScopedDrawable) {
                if (((ScopedDrawable) node).listDrawables().contains(targetDrawable)) {
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


    public static Collection<Drawable> findNodesConnectedToZeroOrOutsideScopeDrawables(FlowGraph graph, ScopedDrawable scope) {
        Collection<Drawable> drawablesInTheScope = scope.listDrawables();
        return drawablesInTheScope.stream().filter(drawable -> {
            List<Drawable> successors = graph.successors(drawable);
            if (successors.isEmpty()) return true;
            return !drawablesInTheScope.containsAll(successors);
        }).collect(toList());
    }

    public static void addToScopeIfNecessary(FlowGraph graph, Drawable adjacentNode, Drawable targetDrawableToAdd) {
        if (adjacentNode instanceof ScopedDrawable) {
            ScopedDrawable scopedDrawable = (ScopedDrawable) adjacentNode;
            scopedDrawable.add(targetDrawableToAdd);
        }
        List<ScopedDrawable> scopedDrawableObjects = ScopeUtilities.findScopesForNode(graph, adjacentNode);
        scopedDrawableObjects.forEach(scopedDrawable -> scopedDrawable.add(targetDrawableToAdd));
    }

    public static int getScopeXEdge(ScopedDrawable scopedDrawable) {
        return scopedDrawable.listDrawables().stream().mapToInt(Drawable::x).max().getAsInt() + Tile.HALF_WIDTH;
    }

    /**
     * It finds the first node outside the given ScopedDrawable.
     */
    public static Optional<Drawable> findFirstNodeOutsideScope(FlowGraph graph, ScopedDrawable scopedDrawable) {
        return findFirstOutsideCollection(graph, scopedDrawable.listDrawables(), scopedDrawable);
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
