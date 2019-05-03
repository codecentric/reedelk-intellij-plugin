package com.esb.plugin.graph.serializer;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;

public class GraphSerializer {

    public static String serialize(FlowGraph graph) {
        JSONArray flow = new JSONArray();
        graph.breadthFirstTraversal(node -> {
            JSONObject component = serialize(node);
            flow.put(component);
        });

        JSONObject flowObject = new JSONObject();
        flowObject.put("id", UUID.randomUUID().toString());
        flowObject.put("flow", flow);
        return flowObject.toString(4);
    }

    private static JSONObject serialize(GraphNode node) {
        return GraphSerializerFactory.get()
                .node(node)
                .build()
                .serialize(node);
    }
}
