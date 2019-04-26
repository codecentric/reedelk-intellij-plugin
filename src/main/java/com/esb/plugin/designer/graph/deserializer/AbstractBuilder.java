package com.esb.plugin.designer.graph.deserializer;

import com.esb.plugin.designer.graph.FlowGraph;

abstract class AbstractBuilder implements Builder {

    protected final FlowGraph graph;
    protected final BuilderContext context;

    public AbstractBuilder(FlowGraph graph, BuilderContext context) {
        this.graph = graph;
        this.context = context;
    }

}
