package com.esb.plugin.editor.designer;

import com.esb.plugin.editor.DesignerEditorPanel;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.components.JBScrollPane;

import java.awt.dnd.DropTarget;
import java.util.TooManyListenersException;

public class ScrollableDesignerPanel extends JBScrollPane {

    private static final Logger LOG = Logger.getInstance(DesignerEditorPanel.class);

    public ScrollableDesignerPanel(DesignerPanel designerPanel) {
        super(designerPanel);

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
