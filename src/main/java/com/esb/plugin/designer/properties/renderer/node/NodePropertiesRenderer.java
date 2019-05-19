package com.esb.plugin.designer.properties.renderer.node;

import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.components.JBPanel;

public interface NodePropertiesRenderer {

    default JBPanel render(GraphNode node) {
        throw new UnsupportedOperationException();
    }

}
