package de.codecentric.reedelk.plugin.editor.designer.hint.strategy;

import de.codecentric.reedelk.plugin.editor.designer.hint.HintResult;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.commons.IsPlaceholderNode;
import de.codecentric.reedelk.plugin.commons.IsScopedGraphNode;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.ImageObserver;

public class HintIsPredecessorOfPlaceholderNode extends BaseStrategy {

    @Override
    public boolean applicable(@NotNull FlowGraph graph,
                              @NotNull HintResult hintResult,
                              @NotNull Graphics2D g2,
                              @NotNull ImageObserver imageObserver) {

        GraphNode hintNode = hintResult.getHintNode();
        Point hintPoint = hintResult.getHintPoint();

        return IsScopedGraphNode.of(hintNode) &&
                graph.successors(hintNode)
                    .stream()
                    .filter(IsPlaceholderNode::of)
                    .anyMatch(placeholder -> placeholder.contains(imageObserver, hintPoint.x, hintPoint.y));
    }

    @Override
    public void draw(@NotNull FlowGraph graph,
                     @NotNull HintResult hintResult,
                     @NotNull Graphics2D g2,
                     @NotNull ImageObserver imageObserver) {
        GraphNode hintNode = hintResult.getHintNode();

        Point hintPoint = hintResult.getHintPoint();
        graph.successors(hintNode)
                .stream()
                .filter(IsPlaceholderNode::of)
                .filter(placeholder -> placeholder.contains(imageObserver, hintPoint.x, hintPoint.y))
                .findFirst()
                .ifPresent(node -> drawPlaceholderHint(g2, node, imageObserver));
    }
}
