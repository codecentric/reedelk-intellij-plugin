package com.esb.plugin.graph.serializer;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.component.domain.ComponentDefaultDescriptor;
import com.esb.plugin.component.type.stop.StopNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONArray;

import static com.esb.internal.commons.Preconditions.checkArgument;

public abstract class AbstractSerializer {

    public static final int JSON_INDENT_FACTOR = 2;

    protected final FlowGraph graph;

    AbstractSerializer(FlowGraph graph) {
        checkArgument(graph != null, "graph");
        this.graph = graph;
    }

    protected abstract String serialize();

    public static class UntilNoSuccessors extends StopNode {
        public UntilNoSuccessors() {
            super(new ComponentData(ComponentDefaultDescriptor.create().build()));
        }
    }

    void serializeFlow(JSONArray flow) {
        // If the graph is empty there is an empty flow
        if (!graph.isEmpty()) {
            GraphNode root = graph.root();
            SerializerFactory.get()
                    .node(root)
                    .build()
                    .serialize(graph, flow, root, new FlowSerializer.UntilNoSuccessors());
        }
    }
}
