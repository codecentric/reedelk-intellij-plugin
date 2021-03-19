package de.codecentric.reedelk.plugin.editor.designer;

import de.codecentric.reedelk.plugin.editor.properties.commons.DisposableScrollPane;

public class ScrollableDesignerPanel extends DisposableScrollPane {

    public ScrollableDesignerPanel(DesignerPanel designerPanel) {
        setViewportView(designerPanel);

        createVerticalScrollBar();
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);

        createHorizontalScrollBar();
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }
}
