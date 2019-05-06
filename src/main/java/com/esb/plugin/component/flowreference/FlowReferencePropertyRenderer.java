package com.esb.plugin.component.flowreference;

import com.esb.plugin.designer.properties.AbstractPropertyRenderer;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.manager.GraphChangeListener;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.components.JBPanel;

public class FlowReferencePropertyRenderer extends AbstractPropertyRenderer {

    public FlowReferencePropertyRenderer(FlowGraph graph, GraphChangeListener listener) {
        super(graph, listener);
    }

    @Override
    public JBPanel render(GraphNode node) {
        return new JBPanel();
    }
}
