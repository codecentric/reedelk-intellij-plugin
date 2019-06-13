package com.esb.plugin.graph.serializer;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.component.domain.ComponentDefaultDescriptor;
import com.esb.plugin.component.type.stop.StopNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONArray;

import static com.esb.internal.commons.Preconditions.checkArgument;

abstract class AbstractSerializer {

    protected final FlowGraph graph;

    AbstractSerializer(FlowGraph graph) {
        checkArgument(graph != null, "graph");
        this.graph = graph;
    }

    protected abstract String serialize();

    protected static class UntilNoSuccessors extends StopNode {
        UntilNoSuccessors() {
            super(new ComponentData(ComponentDefaultDescriptor.create().build()));
        }
    }

    void serializeFlow(JSONArray flow) {
        // If the graph is empty there is an empty flow
        if (!graph.isEmpty()) {
            GraphNode root = graph.root();
            FlowSerializerFactory.get()
                    .node(root)
                    .build()
                    .serialize(graph, flow, root, new FlowSerializer.UntilNoSuccessors());
        }
    }
}
