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

import static com.google.common.base.Preconditions.checkState;
import static java.util.stream.Collectors.toList;

public class ScopeUtilities {

    // TODO: Test this method
    public static Optional<ScopedDrawable> getScopeItIsJoining(FlowGraph graph, Drawable drawable) {
        List<ScopedDrawable> scopedDrawables = graph.nodes()
                .stream()
                .filter(drawable1 -> drawable1 instanceof ScopedDrawable)
                .map(drawable12 -> (ScopedDrawable) drawable12)
                .collect(toList());

        List<ScopedDrawable> scopes = scopedDrawables.stream()
                .filter(scopedDrawable -> {
                    Optional<Drawable> firstNode = getFirstNodeOutsideScope(graph, scopedDrawable);
                    return firstNode.filter(drawable1 -> drawable1 == drawable).isPresent();
                }).collect(toList());

        // TODO: we need to select the outer most amongst all the scopes.
        // The outermost is the one with the lowest X
        if (!scopes.isEmpty()) {
            return Optional.of(scopes.get(0));
        }

        return Optional.empty();
    }

    public static boolean belongToSameScope(FlowGraph graph, Drawable drawable1, Drawable drawable2) {
        Optional<ScopedDrawable> scope1 = findScopeOf(graph, drawable1);
        Optional<ScopedDrawable> scope2 = findScopeOf(graph, drawable2);
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

    public static Optional<ScopedDrawable> findScopeOf(FlowGraph graph, Drawable target) {
        return graph.nodes()
                .stream()
                .filter(drawable -> drawable instanceof ScopedDrawable)
                .map(drawable -> (ScopedDrawable) drawable)
                .filter(scopedDrawable -> scopedDrawable.scopeContains(target))
                .findFirst();
    }

    /**
     * Returns a Stack containing all the scopes the target node belongs to. The topmost
     * element of the stack is the innermost scope this target belongs to. The last element
     * of the stack is the outermost scope this target belongs to.
     *
     * @param graph
     * @param target
     * @return Stack containing all the scopes the target node belongs to.
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

        return findScopes(graph, graph.root(), target, new Stack<>()).orElseGet(Stack::new);
    }

    private static Optional<Stack<ScopedDrawable>> findScopes(@NotNull FlowGraph graph, @NotNull Drawable parent, @NotNull Drawable target, @NotNull Stack<ScopedDrawable> scopesStack) {
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
            Optional<Stack<ScopedDrawable>> scopeFound = findScopes(graph, successor, target, new Stack<>());
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


    public static void addToScopeIfNeeded(FlowGraph graph, Drawable closestPrecedingNode, Connector connector) {
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
     * It finds the first node outside the given ScopedDrawable. By definition a scope block
     * must be followed only by one node.
     *
     * @param graph          the graph this scope belongs to
     * @param scopedDrawable the scope for which we want to find the first node outside its scope
     * @return the first node outside this scope if any. An empty optional otherwise.
     */
    public static Optional<Drawable> getFirstNodeOutsideScope(FlowGraph graph, ScopedDrawable scopedDrawable) {
        Collection<Drawable> lastDrawablesOfScope = listLastDrawablesOfScope(graph, scopedDrawable);
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
        Optional<ScopedDrawable> scope = findScopeOf(graph, drawable);
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
                return 1 + findScopeOf(graph, scopedDrawable)
                        .map(scope -> 1 + countNumberOfNestedScopes(graph, scope))
                        .orElse(0);
            }
        }
        return findScopeOf(graph, drawable)
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
