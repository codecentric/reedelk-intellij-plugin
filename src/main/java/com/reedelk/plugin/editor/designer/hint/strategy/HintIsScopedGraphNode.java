package com.reedelk.plugin.editor.designer.hint.strategy;

import com.reedelk.plugin.commons.IsScopedGraphNode;
import com.reedelk.plugin.editor.designer.hint.HintResult;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopeBoundaries;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.List;

import static com.reedelk.plugin.commons.ScopeUtils.*;

public class HintIsScopedGraphNode extends HintIsGraphNode {

    @Override
    public boolean applicable(@NotNull FlowGraph graph,
                              @NotNull HintResult hintResult,
                              @NotNull Graphics2D g2,
                              @NotNull ImageObserver imageObserver) {
        return IsScopedGraphNode.of(hintResult.getHintNode());
    }

    @Override
    public void draw(@NotNull FlowGraph graph,
                     @NotNull HintResult hintResult,
                     @NotNull Graphics2D g2,
                     @NotNull ImageObserver imageObserver) {
        // If scope is empty, the node must be the first one right outside the scope
        Point hintPoint = hintResult.getHintPoint();
        ScopedGraphNode hint = (ScopedGraphNode) hintResult.getHintNode();
        if (hint.getScope().isEmpty()) {
            emptyScope(graph, g2, hintResult, imageObserver);
        } else {
            notEmptyScope(graph, g2, hintPoint, hint);
        }
    }

    private void emptyScope(@NotNull FlowGraph graph, @NotNull Graphics2D g2, @NotNull HintResult hintResult,
                            @NotNull ImageObserver imageObserver) {
        Point hintPoint = hintResult.getHintPoint();
        ScopedGraphNode scopedGraphNodeHint = ((ScopedGraphNode) hintResult.getHintNode());
        ScopeBoundaries scopeBoundaries = ((ScopedGraphNode) hintResult.getHintNode()).getScopeBoundaries(graph, g2);
        if (hintPoint.x <= scopeBoundaries.getX() + scopeBoundaries.getWidth()) {
            drawVerticalBarHint(graph, g2, scopedGraphNodeHint);
        } else {
            super.draw(graph, hintResult, g2, imageObserver);
        }
    }

    private void notEmptyScope(@NotNull FlowGraph graph, @NotNull Graphics2D g2, Point hintPoint, ScopedGraphNode scopedGraphNodeHint) {
        List<GraphNode> successors = graph.successors(scopedGraphNodeHint);
        for (int successorIndex = 0; successorIndex < successors.size(); successorIndex++) {
            GraphNode successor = successors.get(successorIndex);
            if (isInsideTopArea(graph, successors, successorIndex, scopedGraphNodeHint, hintPoint, g2)) {
                if (scopedGraphNodeHint.isSuccessorAllowedTop(graph, scopedGraphNodeHint, successorIndex)) {
                    drawVerticalBarHint(graph, g2, scopedGraphNodeHint);
                }
                break;
            } else if (isInsideCenterArea(successor, hintPoint, g2)) {
                if (scopedGraphNodeHint.isSuccessorAllowedBefore(graph, scopedGraphNodeHint, successorIndex)) {
                    drawNodeAfterVerticalBarHint(g2, scopedGraphNodeHint, successor);
                }
                break;
            } else if (isInsideBottomArea(graph, successors, successorIndex, scopedGraphNodeHint, hintPoint, g2)) {
                if (scopedGraphNodeHint.isSuccessorAllowedBottom(graph, scopedGraphNodeHint, successorIndex + 1)) {
                    drawVerticalBarHint(graph, g2, scopedGraphNodeHint);
                }
                break;
            }
        }
    }
}
