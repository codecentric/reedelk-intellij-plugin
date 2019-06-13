package com.esb.plugin.component.type.unknown;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.deserializer.AbstractNodeDeserializer;
import com.esb.plugin.graph.deserializer.DeserializerContext;

public class UnknownDeserializer extends AbstractNodeDeserializer {

    public UnknownDeserializer(FlowGraph graph, DeserializerContext context) {
        super(graph, context);
    }
}
