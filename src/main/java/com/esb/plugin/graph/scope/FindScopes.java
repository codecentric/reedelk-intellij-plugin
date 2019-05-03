package com.esb.plugin.graph.scope;

import com.esb.plugin.commons.StackUtils;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
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
    public static Stack<ScopedGraphNode> of(@NotNull FlowGraph graph, @NotNull GraphNode target) {
        Stack<ScopedGraphNode> toReturn = new Stack<>();
        if (target instanceof ScopedGraphNode) {
            toReturn.push((ScopedGraphNode) target);
        }

        Stack<ScopedGraphNode> scopedGraphNodes = _of(graph, target);
        while (!scopedGraphNodes.isEmpty()) {
            toReturn.push(scopedGraphNodes.pop());
        }

        return toReturn;
    }

    private static Stack<ScopedGraphNode> _of(@NotNull FlowGraph graph, @NotNull GraphNode target) {
        Stack<ScopedGraphNode> toReturn = new Stack<>();

        FindScope.of(graph, target).ifPresent(scopedDrawable -> {
            toReturn.push(scopedDrawable);

            Stack<ScopedGraphNode> scopedGraphNodes = _of(graph, scopedDrawable);
            StackUtils.reverse(scopedGraphNodes);
            while (!scopedGraphNodes.isEmpty()) {
                toReturn.push(scopedGraphNodes.pop());
            }
        });
        return toReturn;
    }

}
