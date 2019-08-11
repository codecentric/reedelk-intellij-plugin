package com.reedelk.plugin.editor.designer;

import com.reedelk.plugin.graph.node.GraphNode;

import java.awt.*;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.image.ImageObserver;
import java.util.Optional;

public interface DesignerPanelActionHandler {

    void onMove(Graphics2D graphics, GraphNode selected, Point dragPoint, ImageObserver observer);

    Optional<GraphNode> onAdd(Graphics2D graphics, DropTargetDropEvent dropEvent, ImageObserver observer);

    void onRemove(GraphNode nodeToRemove);
}
