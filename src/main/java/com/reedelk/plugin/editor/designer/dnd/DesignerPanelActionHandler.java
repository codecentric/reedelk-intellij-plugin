package com.reedelk.plugin.editor.designer.dnd;

import com.reedelk.plugin.graph.node.GraphNode;

import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.image.ImageObserver;
import java.util.Optional;

public interface DesignerPanelActionHandler {

    void onRemove(GraphNode nodeToRemove);

    void onMove(Graphics2D graphics, GraphNode selected, Point dragPoint, ImageObserver observer);

    Optional<GraphNode> onAdd(Graphics2D graphics, Point dropPoint, Transferable transferable, ImageObserver observer);
}
