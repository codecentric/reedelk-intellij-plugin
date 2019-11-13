package com.reedelk.plugin.editor.designer.hint;

import com.reedelk.plugin.editor.designer.hint.strategy.EmptyHintNode;
import com.reedelk.plugin.editor.designer.hint.strategy.HintGraphNode;
import com.reedelk.plugin.editor.designer.hint.strategy.HintScopedGraphNode;
import com.reedelk.plugin.editor.designer.hint.strategy.Strategy;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;

import java.awt.*;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

public class HintDrawable {

    private List<Strategy> MOVE_HINT_HANDLERS = unmodifiableList(asList(
            new EmptyHintNode(),
            new HintGraphNode(),
            new HintScopedGraphNode()));

    public void draw(FlowGraph graph, Graphics2D g2, HintResult hintResult, GraphNode selectedNode) {
        MOVE_HINT_HANDLERS.stream()
                .filter(strategy -> strategy.applicable(graph, g2, hintResult, selectedNode))
                .findFirst()
                .ifPresent(strategy -> strategy.draw(graph, g2, hintResult, selectedNode));
    }
}
