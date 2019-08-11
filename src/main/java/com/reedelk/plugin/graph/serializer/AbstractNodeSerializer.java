package com.reedelk.plugin.graph.serializer;

import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import static com.reedelk.runtime.commons.Preconditions.checkState;

public abstract class AbstractNodeSerializer implements Serializer {

    @Override
    public void serialize(FlowGraph graph, JSONArray sequence, GraphNode node, GraphNode stop) {

        JSONObject serializedObject = serializeNode(graph, node, stop);

        sequence.put(serializedObject);

        List<GraphNode> successors = graph.successors(node);

        checkState(successors.size() <= 1,
                "Only scoped nodes might have more than one successors");

        if (!successors.isEmpty()) {

            GraphNode successor = successors.get(0);

            if (stop != successor) {

                SerializerFactory.get()
                        .node(successor)
                        .build()
                        .serialize(graph, sequence, successor, stop);
            }
        }
    }

    protected abstract JSONObject serializeNode(FlowGraph graph, GraphNode node, GraphNode stop);
}
