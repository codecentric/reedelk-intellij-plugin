package com.reedelk.plugin.editor.properties.renderer;

import com.reedelk.plugin.editor.properties.widget.DisposablePanel;
import com.reedelk.plugin.graph.node.GraphNode;

public interface PropertiesRenderer {
    default DisposablePanel render(GraphNode node) {
        throw new UnsupportedOperationException();
    }
}