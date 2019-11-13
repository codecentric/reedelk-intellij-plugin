package com.reedelk.plugin.editor.designer.hint;

import com.reedelk.plugin.graph.node.GraphNode;

import java.awt.*;

public class HintResult {

    public static final HintResult EMPTY = new HintResult(null, null);

    private final Point hintPoint;
    private final GraphNode hintNode;

    HintResult(GraphNode hintNode, Point hintPoint) {
        this.hintNode = hintNode;
        this.hintPoint = hintPoint;
    }

    public GraphNode getHintNode() {
        return hintNode;
    }

    public Point getHintPoint() {
        return hintPoint;
    }
}
