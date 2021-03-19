package de.codecentric.reedelk.plugin.editor.properties.renderer;

import de.codecentric.reedelk.plugin.graph.node.GraphNode;

import javax.swing.*;

public interface ComponentPropertiesRenderer {

    JComponent render(GraphNode node);
}
