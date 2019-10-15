package com.reedelk.plugin.graph.utils;

import com.reedelk.plugin.editor.designer.Drawable;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;

import java.awt.*;

public class FindMaxBottomHalfHeight extends AbstractFindMaxHeight {

    private static final FindMaxBottomHalfHeight INSTANCE = new FindMaxBottomHalfHeight();

    public static int of(FlowGraph graph, Graphics2D graphics, GraphNode start, GraphNode firstNodeOutsideScope) {
        return INSTANCE._of(graph, graphics, start, firstNodeOutsideScope);
    }

    private FindMaxBottomHalfHeight() {
        super(Drawable::bottomHalfHeight);
    }
}