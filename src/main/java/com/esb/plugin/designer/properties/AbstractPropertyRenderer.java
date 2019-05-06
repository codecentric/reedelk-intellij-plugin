package com.esb.plugin.designer.properties;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.manager.GraphChangeListener;

public abstract class AbstractPropertyRenderer implements PropertyRenderer {

    protected final FlowGraph graph;
    protected final GraphChangeListener listener;

    public AbstractPropertyRenderer(FlowGraph graph, GraphChangeListener listener) {
        this.graph = graph;
        this.listener = listener;
    }
}
