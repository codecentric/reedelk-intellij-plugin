package com.reedelk.plugin.editor.designer.hint.strategy;

import com.reedelk.plugin.editor.designer.hint.HintResult;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public interface Strategy {

    boolean applicable(@NotNull FlowGraph graph,
                       @NotNull Graphics2D g2,
                       @NotNull HintResult hintResult,
                       @Nullable GraphNode selectedNode);

    void draw(@NotNull FlowGraph graph,
              @NotNull Graphics2D g2,
              @NotNull HintResult hintResult,
              @Nullable GraphNode selectedNode);

}
