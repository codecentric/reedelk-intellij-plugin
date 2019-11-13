package com.reedelk.plugin.editor.designer.hint.strategy;

import com.reedelk.plugin.commons.IsScopedGraphNode;
import com.reedelk.plugin.editor.designer.hint.HintResult;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopeBoundaries;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

import static com.reedelk.plugin.commons.ScopeUtils.*;

public class HintScopedGraphNode extends HintGraphNode {

    @Override
    public boolean applicable(@NotNull FlowGraph graph, @NotNull Graphics2D g2, @NotNull HintResult hintResult, @Nullable GraphNode selectedNode) {
        return IsScopedGraphNode.of(hintResult.getHintNode());
    }

    @Override
    public void draw(@NotNull FlowGraph graph, @NotNull Graphics2D g2, @NotNull HintResult hintResult, @Nullable GraphNode selectedNode) {
        // If scope is empty, the node must be the first one right outside the scope
        Point hintPoint = hintResult.getHintPoint();
        GraphNode hint = hintResult.getHintNode();

        ScopedGraphNode scopedGraphNode = (ScopedGraphNode) hint;
        if (scopedGraphNode.getScope().isEmpty()) {
            // Draw last scope
            ScopeBoundaries scopeBoundaries = scopedGraphNode.getScopeBoundaries(graph, g2);
            if (hintPoint.x <= scopeBoundaries.getX() + scopeBoundaries.getWidth()) {
                drawHintOnVerticalBar(graph, g2, scopedGraphNode);
            } else {
                super.draw(graph, g2, hintResult, selectedNode);
            }

        } else {
            List<GraphNode> successors = graph.successors(scopedGraphNode);
            for (int successorIndex = 0; successorIndex < successors.size(); successorIndex++) {
                GraphNode successor = successors.get(successorIndex);

                if (isInsideTopArea(graph, successors, successorIndex, scopedGraphNode, hintPoint, g2)) {
                    if (scopedGraphNode.isSuccessorAllowedTop(graph, hint, successorIndex)) {
                        drawHintOnVerticalBar(graph, g2, scopedGraphNode);

                    }
                    return;
                }
                if (isInsideCenterArea(successor, hintPoint, g2)) {
                    // Need to draw
                    if (selectedNode != successor) {
                        drawHintAfterVerticalBar(g2, scopedGraphNode, successor);
                    }
                    return;
                }
                if (isInsideBottomArea(graph, successors, successorIndex, scopedGraphNode, hintPoint, g2)) {
                    if (scopedGraphNode.isSuccessorAllowedBottom(graph, hint, successorIndex + 1)) {
                        if (selectedNode != successor) {
                            drawHintOnVerticalBar(graph, g2, scopedGraphNode);
                        }
                    }
                    return;
                }
            }
        }
    }
}
