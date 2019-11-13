package com.reedelk.plugin.editor.designer.hint.strategy;

import com.reedelk.plugin.editor.designer.hint.HintResult;
import com.reedelk.plugin.graph.FlowGraph;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.ImageObserver;

public interface Strategy {

    boolean applicable(@NotNull FlowGraph graph,
                       @NotNull Graphics2D g2,
                       @NotNull HintResult hintResult,
                       @NotNull ImageObserver imageObserver);

    void draw(@NotNull FlowGraph graph,
              @NotNull Graphics2D g2,
              @NotNull HintResult hintResult,
              @NotNull ImageObserver imageObserver);

}
