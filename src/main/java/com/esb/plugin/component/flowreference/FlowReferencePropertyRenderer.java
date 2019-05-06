package com.esb.plugin.component.flowreference;

import com.esb.plugin.designer.properties.AbstractPropertyRenderer;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.components.JBPanel;

public class FlowReferencePropertyRenderer extends AbstractPropertyRenderer {

    public FlowReferencePropertyRenderer(GraphSnapshot snapshot) {
        super(snapshot);
    }

    @Override
    public JBPanel render(GraphNode node) {
        return new JBPanel();
    }
}
