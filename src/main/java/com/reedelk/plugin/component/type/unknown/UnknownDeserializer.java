package com.reedelk.plugin.component.type.unknown;

import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.deserializer.AbstractNodeDeserializer;
import com.reedelk.plugin.graph.deserializer.DeserializerContext;

public class UnknownDeserializer extends AbstractNodeDeserializer {

    public UnknownDeserializer(FlowGraph graph, DeserializerContext context) {
        super(graph, context);
    }
}
