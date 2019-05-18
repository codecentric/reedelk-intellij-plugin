package com.esb.plugin.component.flowreference;

import com.esb.plugin.component.generic.GenericComponentPropertiesRenderer;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.components.JBPanel;

public class FlowReferencePropertiesRenderer extends GenericComponentPropertiesRenderer {

    public FlowReferencePropertiesRenderer(GraphSnapshot snapshot) {
        super(snapshot);
    }

    @Override
    public JBPanel render(GraphNode node) {
        JBPanel genericProperties = super.render(node);
        return genericProperties;
    }
}
