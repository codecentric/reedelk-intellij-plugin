package de.codecentric.reedelk.plugin.editor.designer.hint.strategy;

import de.codecentric.reedelk.plugin.editor.designer.hint.HintResult;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.ImageObserver;

public class HintIsEmpty implements HintStrategy {

    @Override
    public boolean applicable(@NotNull FlowGraph graph,
                              @NotNull HintResult hintResult, @NotNull Graphics2D g2,
                              @NotNull ImageObserver imageObserver) {
        return hintResult == HintResult.EMPTY;
    }

    @Override
    public void draw(@NotNull FlowGraph graph,
                     @NotNull HintResult hintResult, @NotNull Graphics2D g2,
                     @NotNull ImageObserver imageObserver) {
        // nothing to draw
    }
}
