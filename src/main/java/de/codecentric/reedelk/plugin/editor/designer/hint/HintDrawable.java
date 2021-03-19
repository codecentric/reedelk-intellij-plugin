package de.codecentric.reedelk.plugin.editor.designer.hint;

import de.codecentric.reedelk.plugin.editor.designer.hint.strategy.*;
import de.codecentric.reedelk.plugin.graph.FlowGraph;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

public class HintDrawable {

    private static final List<HintStrategy> handlers = unmodifiableList(asList(
            new HintIsEmpty(),
            new HintIsRoot(),
            new HintIsPredecessorOfPlaceholderNode(),
            new HintIsPlaceholderNode(),
            new HintIsGraphNode(),
            new HintIsScopedGraphNode()));

    public void draw(FlowGraph graph, Graphics2D g2, HintResult hintResult, ImageObserver imageObserver) {
        handlers.stream()
                .filter(strategy -> strategy.applicable(graph, hintResult, g2, imageObserver))
                .findFirst()
                .ifPresent(strategy -> strategy.draw(graph, hintResult, g2, imageObserver));
    }
}
