package com.esb.plugin.component.flowreference;

import com.esb.plugin.designer.properties.renderer.node.AbstractNodePropertiesRenderer;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.components.JBPanel;

public class FlowReferencePropertiesRenderer extends AbstractNodePropertiesRenderer {

    public FlowReferencePropertiesRenderer(GraphSnapshot snapshot) {
        super(snapshot);
    }

    @Override
    public JBPanel render(GraphNode node) {


        return new JBPanel();
    }
}
