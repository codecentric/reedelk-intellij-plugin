package com.esb.plugin.graph.serializer;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.ComponentDescriptor;
import com.esb.plugin.component.stop.StopNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.esb.internal.commons.JsonParser.Flow;
import static com.esb.internal.commons.Preconditions.checkArgument;

public class GraphSerializer {

    private final FlowGraph graph;

    public static String serialize(FlowGraph graph) {
        GraphSerializer serializer = new GraphSerializer(graph);
        return serializer.serialize();
    }

    private GraphSerializer(FlowGraph graph) {
        checkArgument(graph != null, "graph");
        this.graph = graph;
    }

    private String serialize() {
        JSONArray flow = new JSONArray();

        GraphNode root = graph.root();
        GraphSerializerFactory.get()
                .node(root)
                .build()
                .serialize(graph, flow, root, new UntilNoSuccessors());


        JSONObject flowObject = JsonObjectFactory.newJSONObject();
        Flow.id(graph.id(), flowObject);
        Flow.flow(flow, flowObject);
        return flowObject.toString(2);
    }

    static class UntilNoSuccessors extends StopNode {
        UntilNoSuccessors() {
            super(new ComponentData(ComponentDescriptor.create().build()));
        }
    }
}
