package com.reedelk.plugin.editor.designer;

import com.intellij.openapi.diagnostic.Logger;
import com.reedelk.plugin.editor.properties.widget.DisposableScrollPane;

import java.awt.dnd.DropTarget;
import java.util.TooManyListenersException;

public class ScrollableDesignerPanel extends DisposableScrollPane {

    private static final Logger LOG = Logger.getInstance(ScrollableDesignerPanel.class);

    public ScrollableDesignerPanel(DesignerPanel designerPanel) {
        setViewportView(designerPanel);

        createVerticalScrollBar();
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);

        createHorizontalScrollBar();
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);

        registerDropTargetListener(designerPanel);
    }

    private void registerDropTargetListener(DesignerPanel designer) {
        DropTarget dropTarget = new DropTarget();
        designer.setDropTarget(dropTarget);
        try {
            dropTarget.addDropTargetListener(designer);
        } catch (TooManyListenersException e) {
            LOG.error("DropTarget listener error", e);
        }
    }
}
