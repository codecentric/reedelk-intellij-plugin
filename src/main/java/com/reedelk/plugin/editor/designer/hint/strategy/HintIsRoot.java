package com.reedelk.plugin.editor.designer.hint.strategy;

import com.reedelk.plugin.commons.IsPlaceholderNode;
import com.reedelk.plugin.editor.designer.hint.HintResult;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.ImageObserver;

import static com.reedelk.plugin.commons.Images.Component.PlaceholderHintIcon;
import static com.reedelk.plugin.editor.designer.icon.Icon.Dimension.HALF_ICON_WIDTH;
import static com.reedelk.plugin.editor.designer.icon.Icon.Dimension.ICON_HEIGHT;

public class HintIsRoot extends BaseStrategy {

    @Override
    public boolean applicable(@NotNull FlowGraph graph,
                              @NotNull HintResult hintResult,
                              @NotNull Graphics2D g2,
                              @NotNull ImageObserver imageObserver) {
        return hintResult == HintResult.ROOT;
    }

    @Override
    public void draw(@NotNull FlowGraph graph,
                     @NotNull HintResult hintResult,
                     @NotNull Graphics2D g2,
                     @NotNull ImageObserver imageObserver) {
        GraphNode root = graph.root();
        if (IsPlaceholderNode.of(root)) {
            g2.drawImage(PlaceholderHintIcon,
                    root.x() - HALF_ICON_WIDTH,
                    root.y() - ICON_HEIGHT,
                    imageObserver);
        } else {
            drawNodeHint(g2, root);
        }
    }
}
