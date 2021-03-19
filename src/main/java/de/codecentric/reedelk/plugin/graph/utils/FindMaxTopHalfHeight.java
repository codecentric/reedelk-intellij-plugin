package de.codecentric.reedelk.plugin.graph.utils;

import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.editor.designer.Drawable;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;

import java.awt.*;

public class FindMaxTopHalfHeight extends AbstractFindMaxHeight {

    private static final FindMaxTopHalfHeight INSTANCE = new FindMaxTopHalfHeight();

    public static int of(FlowGraph graph, Graphics2D graphics, GraphNode start, GraphNode firstNodeOutsideScope) {
        return INSTANCE.internalOf(graph, graphics, start, firstNodeOutsideScope);
    }

    private FindMaxTopHalfHeight() {
        super(Drawable::topHalfHeight);
    }
}
