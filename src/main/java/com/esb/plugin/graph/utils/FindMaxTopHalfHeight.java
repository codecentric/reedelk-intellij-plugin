package com.esb.plugin.graph.utils;

import com.esb.plugin.editor.designer.Drawable;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;

import java.awt.*;

public class FindMaxTopHalfHeight extends AbstractFindMaxHeight {

    private static final FindMaxTopHalfHeight INSTANCE = new FindMaxTopHalfHeight();

    public static int of(FlowGraph graph, Graphics2D graphics, GraphNode start, GraphNode firstNodeOutsideScope) {
        return INSTANCE._of(graph, graphics, start, firstNodeOutsideScope);
    }

    private FindMaxTopHalfHeight() {
        super(Drawable::topHalfHeight);
    }
}