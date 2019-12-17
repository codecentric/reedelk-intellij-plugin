package com.reedelk.plugin.graph.utils;

import com.reedelk.plugin.editor.designer.Drawable;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;

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
