package com.esb.plugin.component.type.stop;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.deserializer.AbstractNodeDeserializer;
import com.esb.plugin.graph.deserializer.DeserializerContext;

public class StopDeserializer extends AbstractNodeDeserializer {

    public StopDeserializer(FlowGraph graph, DeserializerContext context) {
        super(graph, context);
    }

}
