package com.esb.plugin.designer.editor.designer;

import com.esb.plugin.designer.editor.common.FlowDataStructure;
import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.editor.component.DrawableComponent;
import com.esb.plugin.graph.handler.Drawable;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.IOException;

public class DesignerPanelDropTarget extends DropTarget {


    private final DesignerPanel drawingPanel;
    private final FlowDataStructure flowDataStructure;

    public DesignerPanelDropTarget(FlowDataStructure flowDataStructure, DesignerPanel drawingPanel) {
        this.drawingPanel = drawingPanel;
        this.flowDataStructure = flowDataStructure;
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

        DrawableComponent drawableComponent = new DrawableComponent(component);
        // Here need to decide given the position where this component should go.

        // Find parent
        // If parent is a choice or a fork, then check between siblings where it is
        Drawable drawable = flowDataStructure.closestParentOf(drawableComponent);
        flowDataStructure.add(drawable, drawableComponent);
        flowDataStructure.computeNodesPositions();
        flowDataStructure.notifyChange();
    }

}
