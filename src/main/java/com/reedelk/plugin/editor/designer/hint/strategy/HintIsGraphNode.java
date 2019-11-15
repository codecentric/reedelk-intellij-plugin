package com.reedelk.plugin.editor.designer.hint.strategy;

import com.reedelk.plugin.commons.IsNotScopedGraphNode;
import com.reedelk.plugin.commons.IsScopedGraphNode;
import com.reedelk.plugin.editor.designer.hint.HintResult;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopeBoundaries;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.graph.utils.FindScopes;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.Stack;

public class HintIsGraphNode extends BaseStrategy {

    @Override
    public boolean applicable(@NotNull FlowGraph graph,
                              @NotNull HintResult hintResult,
                              @NotNull Graphics2D g2,
                              @NotNull ImageObserver imageObserver) {
        return IsNotScopedGraphNode.of(hintResult.getHintNode());
    }

    @Override
    public void draw(@NotNull FlowGraph graph,
                     @NotNull HintResult hintResult,
                     @NotNull Graphics2D g2,
                     @NotNull ImageObserver imageObserver) {
        GraphNode hintNode = hintResult.getHintNode();

        Stack<ScopedGraphNode> hintNodeScopes = FindScopes.of(graph, hintNode);
        if (hintNodeScopes.isEmpty()) {
            drawNodeHintAfter(g2, hintNode);
            return;
        }

        Point hintPoint = hintResult.getHintPoint();
        ScopedGraphNode lastScope = null;

        while (!hintNodeScopes.isEmpty()) {
            ScopedGraphNode currentScope = hintNodeScopes.pop();
            ScopeBoundaries currentScopeBoundaries = currentScope.getScopeBoundaries(graph, g2);

            // If the hint point belongs to the current scope:
            //
            // 1. If the current point belongs to this scope and the hint node belongs to it as well,
            //      then we draw the hint (which might be different if the hint node is a scoped node or not).

            // 2. If the current point belongs to this scope but the hint node does not belong to it,
            //      it means that it is probably in a nested scope, and we know that the previous scope met in the loop
            //      is the last one before the hit point. We will use the last scope to draw the hint on its
            //      rightmost scope's border.

            if (hintPoint.x <= currentScopeBoundaries.getX() + currentScopeBoundaries.getWidth()) {

                // The hint point the current scope.

                if (currentScope.scopeContains(hintNode)) {
                    // It might be a scoped graph node because this strategy is used by
                    // a strategy handling scoped graph node as well.
                    if (IsScopedGraphNode.of(hintNode)) {
                        currentScopeBoundaries = ((ScopedGraphNode) hintNode).getScopeBoundaries(graph, g2);
                        drawEndOfScopeHint(g2, currentScopeBoundaries);

                    } else {
                        drawNodeHintAfter(g2, hintNode);
                    }
                    // The hint has been already drawn. We can stop here.
                    return;
                }

                // The scope before 'current scope' referenced in the 'lastScope' variable is
                // the last scope before the hint point and therefore we must stop here.
                break;
            }

            lastScope = currentScope;
        }

        if (lastScope != null) {
            // The hint point is outside the 'last' scope. We draw a vertical bar overlapping
            // the vertical bar of the rightmost scope's border.
            ScopeBoundaries scopeBoundaries = lastScope.getScopeBoundaries(graph, g2);
            drawEndOfScopeHint(g2, scopeBoundaries);
        }
    }
}
