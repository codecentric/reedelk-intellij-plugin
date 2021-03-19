package de.codecentric.reedelk.plugin.editor.designer;

import de.codecentric.reedelk.plugin.graph.node.GraphNode;

import java.awt.*;

public interface DrawableListener {

    void setTheCursor(Cursor cursor);

    void removeComponent(GraphNode nodeToRemove);

}
