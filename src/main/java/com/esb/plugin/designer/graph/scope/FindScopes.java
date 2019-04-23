package com.esb.plugin.designer.graph.scope;

import com.esb.plugin.commons.StackUtils;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.Stack;

public class FindScopes {

    /**
     * Returns a Stack containing all the scopes the target node belongs to. The topmost
     * element of the stack is the innermost scope this target belongs to. The last element
     * of the stack is the outermost scope this target belongs to.
     *
     * @param graph  the graph where the target node belongs to
     * @param target the target node for which we want to find all the scopes containing it
     * @return Stack containing all the scopes the target node belongs to.
     */
    public static Stack<ScopedDrawable> of(@NotNull FlowGraph graph, @NotNull Drawable target) {
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

}
