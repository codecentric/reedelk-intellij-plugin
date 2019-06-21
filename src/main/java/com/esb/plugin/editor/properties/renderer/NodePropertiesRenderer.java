package com.esb.plugin.editor.properties.renderer;

import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.components.JBPanel;

public interface NodePropertiesRenderer {

    default JBPanel render(GraphNode node) {
        throw new UnsupportedOperationException();
    }

}
