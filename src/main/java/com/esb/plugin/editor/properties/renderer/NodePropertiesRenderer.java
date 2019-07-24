package com.esb.plugin.editor.properties.renderer;

import com.esb.plugin.editor.properties.widget.DisposablePanel;
import com.esb.plugin.graph.node.GraphNode;

public interface NodePropertiesRenderer {
    default DisposablePanel render(GraphNode node) {
        throw new UnsupportedOperationException();
    }
}
