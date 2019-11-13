package com.reedelk.plugin.editor.designer.hint.strategy;

import com.intellij.openapi.util.Pair;
import com.reedelk.plugin.commons.IsScopedGraphNode;
import com.reedelk.plugin.editor.designer.hint.HintResult;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopeBoundaries;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.graph.utils.BelongToSameScope;
import com.reedelk.plugin.graph.utils.FindScope;
import com.reedelk.plugin.graph.utils.FindScopes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Optional;
import java.util.Stack;

import static com.reedelk.plugin.editor.designer.hint.HintMode.MOVE;

public class HintGraphNodeSuccessorIsSelectedNode extends BaseStrategy {

    private HintGraphNode hintGraphNode = new HintGraphNode();
    private HintScopedGraphNode hintScopedGraphNode = new HintScopedGraphNode();

    @Override
    public boolean applicable(@NotNull FlowGraph graph, @NotNull Graphics2D g2, @NotNull HintResult hintResult, @Nullable GraphNode selectedNode) {
        return MOVE.equals(hintResult.getHintMode()) &&
                graph.successors(hintResult.getHintNode()).contains(selectedNode);
    }

    @Override
    public void draw(@NotNull FlowGraph graph, @NotNull Graphics2D g2, @NotNull HintResult hintResult, @Nullable GraphNode selectedNode) {
        GraphNode hintNode = hintResult.getHintNode();

        if (!BelongToSameScope.from(graph, selectedNode, hintNode)) {
            Optional<ScopedGraphNode> hintNodeScope = FindScope.of(graph, hintNode);
            if (hintNodeScope.isPresent()) {
                ScopedGraphNode hintNodeScopeNode = hintNodeScope.get();
                ScopeBoundaries hintScopeBoundaries = hintNodeScopeNode.getScopeBoundaries(graph, g2);

                // The hit point is in a different scope than the scope where the
                // hit node is, therefore we must draw. Otherwise, the selection
                // belongs to the same scope, there is no hint to draw, since
                // the node would stay in the same place.
                if (hintResult.getHintPoint().x > hintScopeBoundaries.getX() + hintScopeBoundaries.getWidth()) {
                    // We must check if  it is in the same scope

                    Pair<ScopedGraphNode, ScopedGraphNode> hintScope = findScope(graph, hintResult.getHintPoint(), hintNode, g2);
                    ScopedGraphNode selectedScope = FindScope.of(graph, selectedNode).orElse(null);

                    if (hintScope.second == null && selectedScope == null) {
                        // Nothing to do
                        return;
                    }

                    if (hintScope.second != null && hintScope.second != selectedScope) {
                        // Draw last scope
                        ScopeBoundaries scopeBoundaries = hintScope.first.getScopeBoundaries(graph, g2);
                        drawEndOfScopeHint(g2, scopeBoundaries);
                        return;
                    } else {
                        return;
                    }
                }
            }
        }

        if (IsScopedGraphNode.of(hintNode)) {
            hintScopedGraphNode.draw(graph, g2, hintResult, selectedNode);
        } else {
            hintGraphNode.draw(graph, g2, hintResult, selectedNode);
        }
    }

    private Pair<ScopedGraphNode, ScopedGraphNode> findScope(FlowGraph graph, Point hintPoint, GraphNode hintNode, Graphics2D g2) {
        Stack<ScopedGraphNode> stackOfScopes = FindScopes.of(graph, hintNode);
        ScopedGraphNode beforeLastScope = stackOfScopes.pop();
        ScopedGraphNode lastScope = null;
        while (!stackOfScopes.isEmpty()) {
            lastScope = stackOfScopes.pop();
            ScopeBoundaries scopeBoundaries = lastScope.getScopeBoundaries(graph, g2);
            if (hintPoint.x < scopeBoundaries.getX() + scopeBoundaries.getWidth()) {
                break;
            } else {
                beforeLastScope = lastScope;
                lastScope = null;
            }
        }
        return new Pair<>(beforeLastScope, lastScope);
    }
}
