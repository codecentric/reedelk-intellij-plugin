package com.reedelk.plugin.editor.properties.renderer;

import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.graph.node.GraphNode;

public interface ComponentPropertiesRenderer {

    DisposablePanel render(GraphNode node);
}
