package com.esb.plugin.component.stop;

import com.esb.plugin.designer.properties.renderer.AbstractPropertiesRenderer;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.components.JBPanel;

public class StopPropertiesRenderer extends AbstractPropertiesRenderer {

    public StopPropertiesRenderer(GraphSnapshot snapshot) {
        super(snapshot);
    }

    @Override
    public JBPanel render(GraphNode node) {
        throw new UnsupportedOperationException();
    }
}
