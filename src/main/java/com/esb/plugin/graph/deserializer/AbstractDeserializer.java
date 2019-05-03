package com.esb.plugin.graph.deserializer;

import com.esb.plugin.graph.FlowGraph;

public abstract class AbstractDeserializer implements Deserializer {

    protected final FlowGraph graph;
    protected final DeserializerContext context;

    public AbstractDeserializer(FlowGraph graph, DeserializerContext context) {
        this.graph = graph;
        this.context = context;
    }

}
