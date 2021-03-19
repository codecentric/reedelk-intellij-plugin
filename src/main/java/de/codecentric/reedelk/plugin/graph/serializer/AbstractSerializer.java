package de.codecentric.reedelk.plugin.graph.serializer;

import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.module.descriptor.model.component.ComponentDescriptor;
import de.codecentric.reedelk.plugin.component.ComponentData;
import de.codecentric.reedelk.plugin.component.type.stop.StopNode;
import org.json.JSONArray;

import static de.codecentric.reedelk.runtime.api.commons.Preconditions.checkArgument;

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
            super(new ComponentData(new ComponentDescriptor()));
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
