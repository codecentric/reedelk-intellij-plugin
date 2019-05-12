package com.esb.plugin.graph.serializer;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import static com.esb.internal.commons.Preconditions.checkState;

public abstract class AbstractNodeSerializer implements Serializer {
    @Override
    public void serialize(FlowGraph graph, JSONArray sequence, GraphNode node, GraphNode stop) {
        JSONObject serializedObject = serializeNode(graph, node, stop);

        sequence.put(serializedObject);

        List<GraphNode> successors = graph.successors(node);
        checkState(successors.size() <= 1,
                "Only scoped nodes might have more than one successors");

        if (!successors.isEmpty()) {
            GraphNode successorNode = successors.get(0);
            if (stop instanceof GraphSerializer.UntilNoSuccessors) {
                GraphSerializerFactory.get()
                        .node(successorNode)
                        .build()
                        .serialize(graph, sequence, successorNode, stop);
            } else if (stop != successorNode) {
                GraphSerializerFactory.get()
                        .node(successorNode)
                        .build()
                        .serialize(graph, sequence, successorNode, stop);
            }
        }
    }

    protected abstract JSONObject serializeNode(FlowGraph graph, GraphNode node, GraphNode stop);
}
