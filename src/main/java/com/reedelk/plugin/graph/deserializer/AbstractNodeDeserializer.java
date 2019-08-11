package com.reedelk.plugin.graph.deserializer;

import com.reedelk.plugin.graph.FlowGraph;

public abstract class AbstractNodeDeserializer implements Deserializer {

    protected final FlowGraph graph;
    protected final DeserializerContext context;

    public AbstractNodeDeserializer(FlowGraph graph, DeserializerContext context) {
        this.graph = graph;
        this.context = context;
    }
}
