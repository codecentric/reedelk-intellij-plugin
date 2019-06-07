package com.esb.plugin.editor.designer;

import com.esb.plugin.graph.node.GraphNode;

import java.awt.*;

public interface DrawableListener {

    void setTheCursor(Cursor cursor);

    void removeComponent(GraphNode nodeToRemove);

}
