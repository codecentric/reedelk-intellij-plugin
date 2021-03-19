package de.codecentric.reedelk.plugin.graph.utils;

import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.editor.designer.Drawable;

import java.awt.*;

public class FindMaxBottomHalfHeight extends AbstractFindMaxHeight {

    private static final FindMaxBottomHalfHeight INSTANCE = new FindMaxBottomHalfHeight();

    public static int of(FlowGraph graph, Graphics2D graphics, GraphNode start, GraphNode firstNodeOutsideScope) {
        return INSTANCE.internalOf(graph, graphics, start, firstNodeOutsideScope);
    }

    private FindMaxBottomHalfHeight() {
        super(Drawable::bottomHalfHeight);
    }
}
