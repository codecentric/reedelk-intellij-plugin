package com.esb.plugin.designer.editor.designer;

import com.intellij.ui.components.JBScrollPane;

public class ScrollableDesignerPanel extends JBScrollPane {

    public ScrollableDesignerPanel(DesignerPanel designerPanel) {
        super(designerPanel);

        createHorizontalScrollBar();
        createVerticalScrollBar();
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
    }
}
