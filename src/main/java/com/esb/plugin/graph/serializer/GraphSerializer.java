package com.esb.plugin.graph.serializer;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.scope.FindFirstNodeOutsideScope;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

import static com.esb.internal.commons.Preconditions.checkState;

public class GraphSerializer {

    // TODO: Fix JSON parser to include setters of the following
    // TODO: properties
    public static String serialize(FlowGraph graph) {
        JSONArray flow = new JSONArray();

        GraphNode root = graph.root();
        doSerialize(graph, flow, root);

        JSONObject flowObject = SerializerUtilities.newJSONObject();

        flowObject.put("id", UUID.randomUUID().toString());
        flowObject.put("flow", flow);
        return flowObject.toString(4);
    }

    public static void doSerialize(FlowGraph graph, JSONArray array, GraphNode graphNode) {
        if (graphNode instanceof ScopedGraphNode) {
            serialize(graph, array, (ScopedGraphNode) graphNode);
        } else {
            serialize(graph, array, graphNode);
        }
    }

    private static void serialize(FlowGraph graph, JSONArray array, ScopedGraphNode scopedGraphNode) {
        Serializer serializer = GraphSerializerFactory.get()
                .node(scopedGraphNode)
                .build();

        JSONObject serializedObject = serializer.serialize(graph, scopedGraphNode);
        array.put(serializedObject);
        FindFirstNodeOutsideScope.of(graph, scopedGraphNode)
                .ifPresent(node -> doSerialize(graph, array, node));
    }

    private static void serialize(FlowGraph graph, JSONArray array, GraphNode node) {
        Serializer serializer = GraphSerializerFactory.get()
                .node(node)
                .build();

        JSONObject serializedObject = serializer.serialize(graph, node);
        array.put(serializedObject);
        List<GraphNode> successors = graph.successors(node);
        checkState(successors.size() <= 1,
                "Only scoped nodes might have more than one successors");

        if (!successors.isEmpty()) {
            doSerialize(graph, array, successors.get(0));
        }
    }
}
