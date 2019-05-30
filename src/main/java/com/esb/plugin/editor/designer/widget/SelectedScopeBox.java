package com.esb.plugin.editor.designer.widget;

import com.esb.plugin.graph.node.ScopedGraphNode;
import com.intellij.ui.JBColor;

import java.awt.*;

import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_ROUND;

public class SelectedScopeBox extends ScopeBox {

    public SelectedScopeBox(ScopedGraphNode scopedGraphNode) {
        super(scopedGraphNode,
                JBColor.DARK_GRAY,
                new BasicStroke(1f, CAP_ROUND, JOIN_ROUND, 0, new float[]{3}, 0));
    }
}
