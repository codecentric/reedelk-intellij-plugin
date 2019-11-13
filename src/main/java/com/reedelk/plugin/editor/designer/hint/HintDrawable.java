package com.reedelk.plugin.editor.designer.hint;

import com.reedelk.plugin.editor.designer.hint.strategy.*;
import com.reedelk.plugin.graph.FlowGraph;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

public class HintDrawable {

    private List<Strategy> MOVE_HINT_HANDLERS = unmodifiableList(asList(
            new EmptyHintNode(),
            new PlaceholderGraphNode(),
            new HintGraphNode(),
            new HintScopedGraphNode()));

    public void draw(FlowGraph graph, Graphics2D g2, HintResult hintResult, ImageObserver imageObserver) {
        MOVE_HINT_HANDLERS.stream()
                .filter(strategy -> strategy.applicable(graph, g2, hintResult, imageObserver))
                .findFirst()
                .ifPresent(strategy -> strategy.draw(graph, g2, hintResult, imageObserver));
    }
}
