package com.esb.plugin.designer.editor.designer;

import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.editor.component.DrawableComponent;

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
        clearAutoscroll();
        dropEvent.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
        try {
            String componentName = (String) dropEvent.getTransferable().getTransferData(DataFlavor.stringFlavor);

            Component component = new Component(componentName);
            component.setComponentDescription("A test description");

            drawingPanel.add(new DrawableComponent(component, drawingPanel, dropEvent.getLocation()));
            drawingPanel.repaint();
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
        }
    }
}
