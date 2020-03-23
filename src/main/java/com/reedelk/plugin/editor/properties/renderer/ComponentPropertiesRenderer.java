package com.reedelk.plugin.editor.properties.renderer;

import com.reedelk.plugin.graph.node.GraphNode;

import javax.swing.*;

public interface ComponentPropertiesRenderer {

    JComponent render(GraphNode node);
}
