package com.esb.plugin.component.type.fork;

import com.esb.plugin.component.type.generic.GenericComponentPropertiesRenderer;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.components.JBPanel;

public class ForkPropertiesRenderer extends GenericComponentPropertiesRenderer {

    public ForkPropertiesRenderer(GraphSnapshot snapshot) {
        super(snapshot);
    }

    @Override
    public JBPanel render(GraphNode node) {
        return super.render(node);
    }

}
