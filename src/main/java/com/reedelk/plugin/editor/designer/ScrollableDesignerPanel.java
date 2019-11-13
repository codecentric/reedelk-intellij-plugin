package com.reedelk.plugin.editor.designer;

import com.reedelk.plugin.editor.properties.widget.DisposableScrollPane;

public class ScrollableDesignerPanel extends DisposableScrollPane {

    public ScrollableDesignerPanel(DesignerPanel designerPanel) {
        setViewportView(designerPanel);

        createVerticalScrollBar();
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);

        createHorizontalScrollBar();
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }
}
