package com.esb.plugin.graph.serializer;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.utils.FindFirstNodeOutsideScope;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.esb.internal.commons.JsonParser.Flow;
import static com.esb.internal.commons.Preconditions.checkState;

public class GraphSerializer {

    public static String serialize(FlowGraph graph) {
        JSONArray flow = new JSONArray();

        GraphNode root = graph.root();
        doSerialize(graph, flow, root, null);

        JSONObject flowObject = JsonObjectFactory.newJSONObject();
        Flow.id(UUID.randomUUID().toString(), flowObject);
        Flow.flow(flow, flowObject);
        return flowObject.toString(2);
    }

    public static void doSerialize(FlowGraph graph, JSONArray array, GraphNode graphNode, GraphNode stop) {
        if (graphNode instanceof ScopedGraphNode) {
            serialize(graph, array, (ScopedGraphNode) graphNode);
        } else {
            serialize(graph, array, graphNode, stop);
        }
    }

    private static void serialize(FlowGraph graph, JSONArray array, ScopedGraphNode scopedGraphNode) {
        Serializer serializer = GraphSerializerFactory.get()
                .node(scopedGraphNode)
                .build();

        Optional<GraphNode> firstNodeOutsideScope = FindFirstNodeOutsideScope.of(graph, scopedGraphNode);
        GraphNode stop = firstNodeOutsideScope.orElse(null);

        JSONObject serializedObject = serializer.serialize(graph, scopedGraphNode, stop);
        array.put(serializedObject);

        firstNodeOutsideScope.ifPresent(node -> doSerialize(graph, array, node, null));
    }

    private static void serialize(FlowGraph graph, JSONArray array, GraphNode node, GraphNode stop) {
        Serializer serializer = GraphSerializerFactory.get()
                .node(node)
                .build();

        JSONObject serializedObject = serializer.serialize(graph, node, stop);
        array.put(serializedObject);

        List<GraphNode> successors = graph.successors(node);
        checkState(successors.size() <= 1,
                "Only scoped nodes might have more than one successors");


        if (!successors.isEmpty()) {
            GraphNode successorNode = successors.get(0);
            if (stop != successorNode) {
                doSerialize(graph, array, successorNode, stop);
            }
        }
    }
}
