package com.reedelk.plugin.editor.designer.hint.strategy;

import com.reedelk.plugin.commons.Half;
import com.reedelk.plugin.commons.IsPlaceholderNode;
import com.reedelk.plugin.commons.LiesBetweenTopAndBottom;
import com.reedelk.plugin.editor.designer.hint.HintResult;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.ImageObserver;

public class HintIsRoot extends BaseStrategy {

    @Override
    public boolean applicable(@NotNull FlowGraph graph,
                              @NotNull HintResult hintResult,
                              @NotNull Graphics2D g2,
                              @NotNull ImageObserver imageObserver) {
        return hintResult != HintResult.EMPTY &&
                graph.predecessors(hintResult.getHintNode()).isEmpty();
    }

    @Override
    public void draw(@NotNull FlowGraph graph,
                     @NotNull HintResult hintResult,
                     @NotNull Graphics2D g2,
                     @NotNull ImageObserver imageObserver) {
        GraphNode root = graph.root();
        Point hintPoint = hintResult.getHintPoint();

        // If we are in this area, we are trying to replace the Inbound component.
        // In this case we draw the hint node before the inbound if the hint point
        // is <= root.x + half width.
        int halfWidth = Half.of(root.width(g2));
        if (hintPoint.x <= root.x() + halfWidth && LiesBetweenTopAndBottom.of(root, hintPoint.y, g2)) {

            if (IsPlaceholderNode.of(root)) {
                // We draw the placeholder hint icon if and only if
                // the root node is a Placeholder node.
                drawPlaceholderHint(g2, root, imageObserver);

            } else {
                drawNodeHintBefore(g2, root);
            }

        } else {
            // We are beyond the area where we are going to replace the root node.
            // In this position we are adding a successor to the root node.
            drawNodeHintEnd(g2, root);
        }
    }
}
