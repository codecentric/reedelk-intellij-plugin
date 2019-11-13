package com.reedelk.plugin.editor.designer.hint.strategy;

import com.reedelk.plugin.commons.IsPlaceholderNode;
import com.reedelk.plugin.commons.IsScopedGraphNode;
import com.reedelk.plugin.editor.designer.hint.HintResult;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.ImageObserver;

import static com.reedelk.plugin.commons.Images.Component.PlaceholderHintIcon;
import static com.reedelk.plugin.editor.designer.icon.Icon.Dimension.HALF_ICON_WIDTH;
import static com.reedelk.plugin.editor.designer.icon.Icon.Dimension.ICON_HEIGHT;

public class PlaceholderGraphNode implements Strategy {

    @Override
    public boolean applicable(@NotNull FlowGraph graph,
                              @NotNull Graphics2D g2,
                              @NotNull HintResult hintResult,
                              @NotNull ImageObserver imageObserver) {

        GraphNode hintNode = hintResult.getHintNode();
        Point hintPoint = hintResult.getHintPoint();

        if (IsScopedGraphNode.of(hintNode)) {
            return graph.successors(hintNode)
                    .stream()
                    .filter(IsPlaceholderNode::of)
                    .anyMatch(placeholder -> placeholder.contains(imageObserver, hintPoint.x, hintPoint.y));

        } else {
            // Strategy applicable if and only if the hint node
            // is a placeholder AND the hint point belongs within
            // the Placeholder icon box.
            return IsPlaceholderNode.of(hintNode) &&
                    hintNode.contains(imageObserver,
                            hintPoint.x,
                            hintPoint.y);
        }
    }

    @Override
    public void draw(@NotNull FlowGraph graph,
                     @NotNull Graphics2D g2,
                     @NotNull HintResult hintResult,
                     @NotNull ImageObserver imageObserver) {
        GraphNode hintNode = hintResult.getHintNode();

        if (IsScopedGraphNode.of(hintNode)) {
            Point hintPoint = hintResult.getHintPoint();
            graph.successors(hintNode)
                    .stream()
                    .filter(IsPlaceholderNode::of)
                    .filter(placeholder -> placeholder.contains(imageObserver, hintPoint.x, hintPoint.y))
                    .findFirst()
                    .ifPresent(node -> g2.drawImage(PlaceholderHintIcon,
                            node.x() - HALF_ICON_WIDTH,
                            node.y() - ICON_HEIGHT,
                            imageObserver));

        } else {
            g2.drawImage(PlaceholderHintIcon,
                    hintNode.x() - HALF_ICON_WIDTH,
                    hintNode.y() - ICON_HEIGHT,
                    imageObserver);
        }
    }
}
