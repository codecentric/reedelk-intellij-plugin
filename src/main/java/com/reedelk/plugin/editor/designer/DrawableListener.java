package com.reedelk.plugin.editor.designer;

import com.reedelk.plugin.graph.node.GraphNode;

import java.awt.*;

public interface DrawableListener {

    void setTheCursor(Cursor cursor);

    void removeComponent(GraphNode nodeToRemove);

}
