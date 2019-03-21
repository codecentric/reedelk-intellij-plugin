package com.esb.plugin.designer.editor.designer;

import com.esb.plugin.designer.editor.component.Component;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.IOException;

public class DesignerPanelDropTarget extends DropTarget {


    private final DesignerPanel drawingPanel;

    public DesignerPanelDropTarget(DesignerPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
    }

    @Override
    public synchronized void drop(DropTargetDropEvent dtde) {
        clearAutoscroll();
        dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
        try {
            Object componentName = dtde.getTransferable().getTransferData(DataFlavor.stringFlavor);
            drawingPanel.add(new Component((String) componentName, drawingPanel, dtde.getLocation()));
            drawingPanel.repaint();
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
        }
    }
}
