package com.esb.plugin.graph.serializer;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.utils.FindFirstNodeOutsideCurrentScope;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkState;

public abstract class AbstractScopedNodeSerializer implements Serializer {

    @Override
    public void serialize(FlowGraph graph, JSONArray sequence, GraphNode node, GraphNode stop) {
        checkState(node instanceof ScopedGraphNode, "Expected ScopedGraphNode");

        Optional<GraphNode> firstNodeOutsideScope = FindFirstNodeOutsideCurrentScope.of(graph, (ScopedGraphNode) node);
        GraphNode firstStop = firstNodeOutsideScope.orElse(stop);

        JSONObject serializedObject = serializeScopedNode(graph, (ScopedGraphNode) node, firstStop);
        sequence.put(serializedObject);

        firstNodeOutsideScope
                .ifPresent(n -> FlowSerializerFactory.get()
                        .node(n)
                        .build()
                        .serialize(graph, sequence, n, stop));
    }

    protected abstract JSONObject serializeScopedNode(FlowGraph graph, ScopedGraphNode scopedNode, GraphNode stop);

}
