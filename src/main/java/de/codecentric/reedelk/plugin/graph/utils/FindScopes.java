package de.codecentric.reedelk.plugin.graph.utils;

import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.node.ScopedGraphNode;
import de.codecentric.reedelk.plugin.commons.IsScopedGraphNode;
import de.codecentric.reedelk.plugin.commons.StackUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Stack;

public class FindScopes {

    private FindScopes() {
    }

    /**
     * Returns a Stack containing all the scopes the target node belongs to. The topmost
     * element of the stack is the innermost scope this target belongs to. The last element
     * of the stack is the outermost scope this target belongs to.
     *
     * @param graph  the graph where the target node belongs to
     * @param target the target node for which we want to find all the scopes containing it
     * @return Stack containing all the scopes the target node belongs to.
     */
    public static Stack<ScopedGraphNode> of(@NotNull FlowGraph graph, @NotNull GraphNode target) {
        Stack<ScopedGraphNode> scopedGraphNodes = internalOf(graph, target);
        Stack<ScopedGraphNode> toReturn = new Stack<>();
        while (!scopedGraphNodes.isEmpty()) {
            toReturn.push(scopedGraphNodes.pop());
        }

        // The target node, if it is a scoped node, then it is on top of the stack.
        if (IsScopedGraphNode.of(target)) {
            toReturn.push((ScopedGraphNode) target);
        }
        return toReturn;
    }

    private static Stack<ScopedGraphNode> internalOf(@NotNull FlowGraph graph, @NotNull GraphNode target) {
        Stack<ScopedGraphNode> toReturn = new Stack<>();

        FindScope.of(graph, target).ifPresent(scopedDrawable -> {
            toReturn.push(scopedDrawable);

            Stack<ScopedGraphNode> scopedGraphNodes = internalOf(graph, scopedDrawable);
            StackUtils.reverse(scopedGraphNodes);
            while (!scopedGraphNodes.isEmpty()) {
                toReturn.push(scopedGraphNodes.pop());
            }
        });
        return toReturn;
    }
}