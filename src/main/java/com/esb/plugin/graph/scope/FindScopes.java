package com.esb.plugin.graph.scope;

import com.esb.plugin.commons.StackUtils;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedNode;
import org.jetbrains.annotations.NotNull;

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
    public static Stack<ScopedNode> of(@NotNull FlowGraph graph, @NotNull GraphNode target) {
        Stack<ScopedNode> toReturn = new Stack<>();
        if (target instanceof ScopedNode) {
            toReturn.push((ScopedNode) target);
        }

        Stack<ScopedNode> scopedNodes = _of(graph, target);
        while (!scopedNodes.isEmpty()) {
            toReturn.push(scopedNodes.pop());
        }

        return toReturn;
    }

    private static Stack<ScopedNode> _of(@NotNull FlowGraph graph, @NotNull GraphNode target) {
        Stack<ScopedNode> toReturn = new Stack<>();

        FindScope.of(graph, target).ifPresent(scopedDrawable -> {
            toReturn.push(scopedDrawable);

            Stack<ScopedNode> scopedNodes = _of(graph, scopedDrawable);
            StackUtils.reverse(scopedNodes);
            while (!scopedNodes.isEmpty()) {
                toReturn.push(scopedNodes.pop());
            }
        });
        return toReturn;
    }

}
