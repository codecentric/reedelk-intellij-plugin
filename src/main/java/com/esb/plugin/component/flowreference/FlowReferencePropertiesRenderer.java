package com.esb.plugin.component.flowreference;

import com.esb.plugin.designer.properties.renderer.AbstractPropertiesRenderer;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.components.JBPanel;

public class FlowReferencePropertiesRenderer extends AbstractPropertiesRenderer {

    public FlowReferencePropertiesRenderer(GraphSnapshot snapshot) {
        super(snapshot);
    }

    @Override
    public JBPanel render(GraphNode node) {
        return new JBPanel();
    }
}
