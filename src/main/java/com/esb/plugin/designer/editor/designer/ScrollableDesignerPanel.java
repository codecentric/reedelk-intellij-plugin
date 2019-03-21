package com.esb.plugin.designer.editor.designer;

import com.intellij.ui.components.JBScrollPane;

public class ScrollableDesignerPanel extends JBScrollPane {

    public ScrollableDesignerPanel() {
        super(new DesignerPanel());

        createHorizontalScrollBar();
        createVerticalScrollBar();
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
    }
}
