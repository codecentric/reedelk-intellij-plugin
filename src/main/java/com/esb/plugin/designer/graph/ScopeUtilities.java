package com.esb.plugin.designer.graph;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.graph.connector.Connector;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static java.util.stream.Collectors.toList;

public class ScopeUtilities {

    public static boolean haveSameScope(FlowGraph graph, Drawable drawable1, Drawable drawable2) {
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

    public static Optional<ScopedDrawable> findScope(FlowGraph graph, Drawable target) {
        return graph.nodes()
                .stream()
                .filter(drawable -> drawable instanceof ScopedDrawable)
                .map(drawable -> (ScopedDrawable) drawable)
                .filter(scopedDrawable -> scopedDrawable.scopeContains(target))
                .findFirst();
    }

    // A node might belong to multiple scopes....
    public static List<ScopedDrawable> findScopesForNode(FlowGraph graph, Drawable targetDrawable) {
        List<ScopedDrawable> scopedDrawables = new ArrayList<>();
        Collection<Drawable> nodes = graph.nodes();
        for (Drawable node : nodes) {
            if (node instanceof ScopedDrawable) {
                if (((ScopedDrawable) node).getScope().contains(targetDrawable)) {
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

    public static void addToScopeIfNecessary(FlowGraph graph, Drawable closestPrecedingNode, Connector connector) {
        if (closestPrecedingNode instanceof ScopedDrawable) {
            ScopedDrawable scopedDrawable = (ScopedDrawable) closestPrecedingNode;
            connector.addToScope(scopedDrawable);
        } else {
            List<ScopedDrawable> scopedDrawableObjects = ScopeUtilities.findScopesForNode(graph, closestPrecedingNode);
            scopedDrawableObjects.forEach(connector::addToScope);
        }
    }

    public static int getMaxScopeXBound(@NotNull FlowGraph graph, @NotNull ScopedDrawable scopedDrawable) {
        Collection<Drawable> lastDrawablesOfScope = listLastDrawablesOfScope(graph, scopedDrawable);
        int maxX = scopedDrawable.x();
        Drawable drawableWithMaxX = scopedDrawable;
        for (Drawable lastDrawableOfScope : lastDrawablesOfScope) {
            if (lastDrawableOfScope.x() >= maxX) {
                maxX = lastDrawableOfScope.x();
                drawableWithMaxX = lastDrawableOfScope;
            }
        }
        return drawableWithMaxX.x() + Tile.HALF_WIDTH;
    }

    /**
     * It finds the first node outside the given ScopedDrawable.
     */
    public static Collection<Drawable> listFirstDrawablesOutsideScope(FlowGraph graph, ScopedDrawable scopedDrawable) {
        Collection<Drawable> lastDrawablesOfScope = listLastDrawablesOfScope(graph, scopedDrawable);
        List<Drawable> firstDrawablesOutsideScope = new ArrayList<>();
        lastDrawablesOfScope.forEach(lastDrawableOfScope -> {
            List<Drawable> successors = graph.successors(lastDrawableOfScope);
            firstDrawablesOutsideScope.addAll(successors);
        });
        return firstDrawablesOutsideScope;
    }

    public static Collection<Drawable> listLastDrawablesOfScope(FlowGraph graph, ScopedDrawable scopedDrawable) {
        // Collect all ScopedDrawable
        Collection<Drawable> allElementsInScopeAndNestedScopes = collectAllScopedDrawableElements(scopedDrawable);
        return allElementsInScopeAndNestedScopes.stream().filter(drawable -> {
            List<Drawable> successors = graph.successors(drawable);
            if (successors.isEmpty()) return true;
            // If exists at least one
            return !allElementsInScopeAndNestedScopes.containsAll(successors);
        }).collect(toList());
    }

    public static Collection<Drawable> findNodesConnectedToZeroOrOutsideScopeDrawables(FlowGraph graph, ScopedDrawable scope) {
        Collection<Drawable> drawablesInTheScope = scope.getScope();
        return drawablesInTheScope
                .stream()
                .filter(drawable -> {
                    List<Drawable> successors = graph.successors(drawable);
                    if (successors.isEmpty()) return true;
                    return !drawablesInTheScope.containsAll(successors);
                })
                .collect(toList());
    }

    private static Collection<Drawable> collectAllScopedDrawableElements(ScopedDrawable scopedDrawable) {
        Collection<Drawable> scope = scopedDrawable.getScope();
        Set<Drawable> allElements = new HashSet<>(scope);
        Set<Drawable> nested = new HashSet<>();
        allElements.forEach(drawable -> {
            if (drawable instanceof ScopedDrawable) {
                nested.addAll(collectAllScopedDrawableElements((ScopedDrawable) drawable));
            }
        });
        allElements.addAll(nested);
        allElements.add(scopedDrawable);
        return allElements;
    }

    public static boolean isLastOfScope(FlowGraph graph, Drawable drawable) {
        if (drawable instanceof ScopedDrawable) {
            if (graph.successors(drawable).isEmpty()) {
                // A ScopedDrawable node is last in
                // the scope if it does not have children.
                return true;
            }
        }
        Optional<ScopedDrawable> scope = findScope(graph, drawable);
        if (!scope.isPresent()) return false;

        List<Drawable> successors = graph.successors(drawable);
        if (successors.isEmpty()) return true;
        return successors
                .stream()
                .anyMatch(d -> !scope.get().scopeContains(d));
    }

    public static int countNumberOfNestedScopes(FlowGraph graph, Drawable drawable) {
        if (drawable instanceof ScopedDrawable) {
            ScopedDrawable scopedDrawable = (ScopedDrawable) drawable;
            if (scopedDrawable.getScope().isEmpty()) {
                return 1 + findScope(graph, scopedDrawable)
                        .map(d -> 1 + countNumberOfNestedScopes(graph, d))
                        .orElse(0);
            }
        }
        return findScope(graph, drawable)
                .map(scopedDrawable -> 1 + countNumberOfNestedScopes(graph, scopedDrawable))
                .orElse(0);
    }

    public static Optional<Integer> scopesBetween(ScopedDrawable scopedDrawable, Drawable target) {
        return scopesBetween(0, scopedDrawable, target);
    }

    public static Optional<Integer> scopesBetween(int depth, ScopedDrawable scopedDrawable, Drawable target) {
        if (scopedDrawable == target) return Optional.of(depth);
        if (scopedDrawable.getScope().isEmpty()) {
            return Optional.of(depth);
        }
        for (Drawable drawableInScope : scopedDrawable.getScope()) {
            if (drawableInScope instanceof ScopedDrawable) {
                return scopesBetween(depth + 1, (ScopedDrawable) drawableInScope, target);
            } else if (drawableInScope == target) {
                return Optional.of(depth);
            }
        }
        return Optional.empty();
    }
}
