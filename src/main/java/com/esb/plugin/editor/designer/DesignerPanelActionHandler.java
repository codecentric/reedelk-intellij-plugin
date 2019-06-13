package com.esb.plugin.editor.designer;

import com.esb.plugin.graph.node.GraphNode;

import java.awt.*;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.image.ImageObserver;

public interface DesignerPanelActionHandler {

    void onMove(Graphics2D graphics, GraphNode selected, Point dragPoint, ImageObserver observer);

    void onDrop(Graphics2D graphics, DropTargetDropEvent dropEvent, ImageObserver observer);

    void onRemove(GraphNode nodeToRemove);
}
