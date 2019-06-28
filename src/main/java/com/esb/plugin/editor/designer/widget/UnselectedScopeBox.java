package com.esb.plugin.editor.designer.widget;

import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;

import java.awt.*;

public class UnselectedScopeBox extends ScopeBox {

    public UnselectedScopeBox() {
        super(new JBColor(Gray._235, Gray._30), new BasicStroke(1f));
    }
}
