package com.esb.plugin.editor.designer;

import com.esb.plugin.graph.node.GraphNode;

import java.awt.*;
import java.awt.event.MouseEvent;

public interface DrawableListener {

    void select(GraphNode node, MouseEvent event);

    void setTheCursor(Cursor cursor);

    void removeComponent(GraphNode nodeToRemove);
}
