package com.esb.plugin.designer.graph;

import com.esb.plugin.commons.StackUtils;
import com.esb.plugin.designer.graph.connector.Connector;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.ScopeBoundaries;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.*;

import static java.util.stream.Collectors.toList;

public class ScopeUtilities {

    public static boolean belongToSameScope(FlowGraph graph, Drawable drawable1, Drawable drawable2) {
        Optional<ScopedDrawable> scope1 = findScope(graph, drawable1);
        Optional<ScopedDrawable> scope2 = findScope(graph, drawable2);
        if (!scope1.isPresent() && !scope2.isPresent()) {
            // they both don't belong to ANY scope.
            return true;
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

    /*
     * Returns a Stack containing all the scopes the target node belongs to. The topmost
     * element of the stack is the innermost scope this target belongs to. The last element
     * of the stack is the outermost scope this target belongs to.
     */
    public static Stack<ScopedDrawable> findScopesOf(@NotNull FlowGraph graph, @NotNull Drawable target) {
        boolean targetBelongsToScope = graph.nodes()
                .stream()
                .filter(drawable -> drawable instanceof ScopedDrawable)
                .map(drawable -> (ScopedDrawable) drawable)
                .anyMatch(scopedDrawable -> scopedDrawable.scopeContains(target));

        if (!targetBelongsToScope) {
            if (target instanceof ScopedDrawable) {
                Stack<ScopedDrawable> singleElement = new Stack();
                singleElement.push((ScopedDrawable) target);
                return singleElement;
            }
            return new Stack<>();
        }

        Optional<Stack<ScopedDrawable>> targetScopes = doFindTargetScopes(graph, graph.root(), target, new Stack<>());
        return targetScopes.orElseGet(Stack::new);
    }

    private static Optional<Stack<ScopedDrawable>> doFindTargetScopes(@NotNull FlowGraph graph, @NotNull Drawable parent, @NotNull Drawable target, @NotNull Stack<ScopedDrawable> scopesStack) {
        List<Drawable> successors = graph.successors(parent);
        for (Drawable successor : successors) {
            if (successor instanceof ScopedDrawable) {
                scopesStack.push((ScopedDrawable) successor);
            }
            if (successor == target) {
                return Optional.of(scopesStack);
            }
            if (successor instanceof ScopedDrawable) {
                if (((ScopedDrawable) successor).scopeContains(target)) {
                    return Optional.of(scopesStack);
                }
            }
            Optional<Stack<ScopedDrawable>> scopeFound =
                    doFindTargetScopes(graph, successor, target, new Stack<>());
            if (scopeFound.isPresent()) {
                StackUtils.reverse(scopeFound.get());
                while (!scopeFound.get().isEmpty()) {
                    scopesStack.push(scopeFound.get().pop());
                }
                return Optional.of(scopesStack);
            }
        }
        return Optional.empty();
    }


    public static void addToScopeIfNecessary(FlowGraph graph, Drawable closestPrecedingNode, Connector connector) {
        if (closestPrecedingNode instanceof ScopedDrawable) {
            ScopedDrawable scopedDrawable = (ScopedDrawable) closestPrecedingNode;
            connector.addToScope(scopedDrawable);
        } else {
            List<ScopedDrawable> scopedDrawableObjects = findScopesOf(graph, closestPrecedingNode);
            scopedDrawableObjects.forEach(connector::addToScope);
        }
    }

    public static int getScopeMaxXBound(@NotNull FlowGraph graph, @NotNull ScopedDrawable scopedDrawable, @NotNull Graphics2D graphics) {
        ScopeBoundaries scopeBoundaries = scopedDrawable.getScopeBoundaries(graph, graphics);
        return scopeBoundaries.getX() + scopeBoundaries.getWidth();
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
                Optional<Integer> found = scopesBetween(depth + 1, (ScopedDrawable) drawableInScope, target);
                if (found.isPresent()) return found;
            } else if (drawableInScope == target) {
                return Optional.of(depth);
            }
        }
        return Optional.empty();
    }
}
