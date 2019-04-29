package com.esb.plugin.designer.canvas;

import com.esb.plugin.designer.EditorPanel;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.components.JBScrollPane;

import java.awt.dnd.DropTarget;
import java.util.TooManyListenersException;

public class ScrollableCanvasPanel extends JBScrollPane {

    private static final Logger LOG = Logger.getInstance(EditorPanel.class);

    public ScrollableCanvasPanel(CanvasPanel canvasPanel) {
        super(canvasPanel);

        createVerticalScrollBar();
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);

        createHorizontalScrollBar();
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);

        registerDropTargetListener(canvasPanel);
    }

    private void registerDropTargetListener(CanvasPanel designer) {
        DropTarget dropTarget = new DropTarget();
        designer.setDropTarget(dropTarget);
        try {
            dropTarget.addDropTargetListener(designer);
        } catch (TooManyListenersException e) {
            LOG.error("DropTarget listener error", e);
        }
    }
}
