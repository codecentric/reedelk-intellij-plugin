package de.codecentric.reedelk.plugin.editor.designer.hint.strategy;

import de.codecentric.reedelk.plugin.editor.designer.hint.HintResult;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.commons.IsPlaceholderNode;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.ImageObserver;

public class HintIsPlaceholderNode extends BaseStrategy {

    @Override
    public boolean applicable(@NotNull FlowGraph graph,
                              @NotNull HintResult hintResult,
                              @NotNull Graphics2D g2,
                              @NotNull ImageObserver imageObserver) {

        GraphNode hintNode = hintResult.getHintNode();
        Point hintPoint = hintResult.getHintPoint();

        // Strategy applicable if and only if the hint node
        // is a placeholder AND the hint point belongs within
        // the Placeholder icon box.
        return IsPlaceholderNode.of(hintNode) &&
                hintNode.contains(imageObserver,
                        hintPoint.x,
                        hintPoint.y);
    }

    @Override
    public void draw(@NotNull FlowGraph graph,
                     @NotNull HintResult hintResult,
                     @NotNull Graphics2D g2,
                     @NotNull ImageObserver imageObserver) {

        GraphNode hintNode = hintResult.getHintNode();
        drawPlaceholderHint(g2, hintNode, imageObserver);
    }
}
