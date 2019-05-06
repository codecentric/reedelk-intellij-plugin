package com.esb.plugin.component.stop;

import com.esb.plugin.designer.properties.AbstractPropertyRenderer;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.manager.GraphChangeListener;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.components.JBPanel;

public class StopPropertyRenderer extends AbstractPropertyRenderer {

    public StopPropertyRenderer(FlowGraph graph, GraphChangeListener listener) {
        super(graph, listener);
    }

    @Override
    public JBPanel render(GraphNode node) {
        throw new UnsupportedOperationException();
    }
}
