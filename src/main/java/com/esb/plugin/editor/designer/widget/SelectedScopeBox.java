package com.esb.plugin.editor.designer.widget;

import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;

import java.awt.*;

public class SelectedScopeBox extends ScopeBox {

    public SelectedScopeBox() {
        super(new JBColor(Gray._170, Gray._30), new BasicStroke(1f));
    }
}
