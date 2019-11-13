package com.reedelk.plugin.editor.designer.hint;

import com.reedelk.plugin.graph.node.GraphNode;

import java.awt.*;

public class HintResult {

    public static final HintResult EMPTY = new HintResult(null, null, null);

    private final Point hintPoint;
    private final HintMode hintMode;
    private final GraphNode hintNode;

    HintResult(GraphNode hintNode, Point hintPoint, HintMode hintMode) {
        this.hintNode = hintNode;
        this.hintMode = hintMode;
        this.hintPoint = hintPoint;
    }

    public GraphNode getHintNode() {
        return hintNode;
    }

    public HintMode getHintMode() {
        return hintMode;
    }

    public Point getHintPoint() {
        return hintPoint;
    }
}
