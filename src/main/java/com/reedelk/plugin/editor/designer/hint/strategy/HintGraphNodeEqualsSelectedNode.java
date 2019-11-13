package com.reedelk.plugin.editor.designer.hint.strategy;

import com.reedelk.plugin.commons.IsNotScopedGraphNode;
import com.reedelk.plugin.editor.designer.hint.HintResult;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopeBoundaries;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.graph.utils.FindScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Optional;

import static com.reedelk.plugin.editor.designer.hint.HintMode.MOVE;

public class HintGraphNodeEqualsSelectedNode extends HintGraphNode {

    @Override
    public boolean applicable(@NotNull FlowGraph graph, @NotNull Graphics2D g2, @NotNull HintResult hintResult, @Nullable GraphNode selectedNode) {
        return IsNotScopedGraphNode.of(hintResult.getHintNode()) &&
                MOVE.equals(hintResult.getHintMode()) &&
                hintResult.getHintNode() == selectedNode;
    }

    @Override
    public void draw(@NotNull FlowGraph graph, @NotNull Graphics2D g2, @NotNull HintResult hintResult, @Nullable GraphNode selectedNode) {
        Optional<ScopedGraphNode> hintNodeScope = FindScope.of(graph, hintResult.getHintNode());
        if (hintNodeScope.isPresent()) {
            ScopedGraphNode scopedGraphNode = hintNodeScope.get();
            ScopeBoundaries scopeBoundaries = scopedGraphNode.getScopeBoundaries(graph, g2);

            // The hit point is in a different scope than the scope where the
            // hit node is, therefore we must draw. Otherwise, the selection
            // belongs to the same scope, there is no hint to draw, since
            // the node would stay in the same place.
            if (hintResult.getHintPoint().x > scopeBoundaries.getX() + scopeBoundaries.getWidth()) {
                super.draw(graph, g2, hintResult, selectedNode);
            }
        }
    }
}
