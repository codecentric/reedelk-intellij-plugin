package com.esb.plugin.editor.designer.widget;

import com.esb.plugin.graph.node.ScopedGraphNode;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;

import java.awt.*;

public class UnselectedScopeBox extends ScopeBox {
    public UnselectedScopeBox(ScopedGraphNode scopedGraphNode) {
        super(scopedGraphNode, new JBColor(Gray._235, Gray._30), new BasicStroke(1f));
    }
}
