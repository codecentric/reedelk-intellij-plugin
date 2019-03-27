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
    public synchronized void drop(DropTargetDropEvent dropEvent) {
        String componentName;
        try {
            componentName = (String) dropEvent.getTransferable().getTransferData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            dropEvent.rejectDrop();
            return;
        }

        clearAutoscroll();
        dropEvent.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

        Component component = new Component(componentName);
        component.setDescription("A description");


        // TODO: Here need to decide given the position where this component should go in the tree
    }

}
