package com.reedelk.plugin.editor.designer.hint.strategy;

import com.reedelk.plugin.commons.IsNotScopedGraphNode;
import com.reedelk.plugin.editor.designer.hint.HintResult;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopeBoundaries;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.graph.utils.FindScopes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Stack;

public class HintGraphNode extends BaseStrategy {

    @Override
    public boolean applicable(@NotNull FlowGraph graph, @NotNull Graphics2D g2, @NotNull HintResult hintResult, @Nullable GraphNode selectedNode) {
        return IsNotScopedGraphNode.of(hintResult.getHintNode());
    }

    @Override
    public void draw(@NotNull FlowGraph graph, @NotNull Graphics2D g2, @NotNull HintResult hintResult, @Nullable GraphNode selectedNode) {

        GraphNode hintNode = hintResult.getHintNode();

        Stack<ScopedGraphNode> stackOfScopes = FindScopes.of(graph, hintNode);
        if (stackOfScopes.isEmpty()) {
            drawHint(g2, hintNode);
            return;
        }

        Point hintPoint = hintResult.getHintPoint();
        ScopedGraphNode lastScope = null;

        while (!stackOfScopes.isEmpty()) {
            ScopedGraphNode currentScope = stackOfScopes.pop();
            ScopeBoundaries scopeBoundaries = currentScope.getScopeBoundaries(graph, g2);
            if (hintPoint.x <= scopeBoundaries.getX() + scopeBoundaries.getWidth()) {
                // Hit point belongs to same scope

                if (currentScope.scopeContains(hintNode)) {
                    if (hintNode instanceof ScopedGraphNode) {
                        scopeBoundaries = ((ScopedGraphNode) hintNode).getScopeBoundaries(graph, g2);
                        drawEndOfScopeHint(g2, scopeBoundaries);
                    } else {
                        drawHint(g2, hintNode);
                    }
                    return;
                }
                break;
            }

            lastScope = currentScope;
        }
        if (lastScope != null) {
            // Draw last scope
            ScopeBoundaries scopeBoundaries = lastScope.getScopeBoundaries(graph, g2);
            drawEndOfScopeHint(g2, scopeBoundaries);
        }
    }
}
