package de.codecentric.reedelk.plugin.graph.serializer;

import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.node.ScopedGraphNode;
import de.codecentric.reedelk.plugin.graph.utils.FindFirstNodeOutsideCurrentScope;
import de.codecentric.reedelk.plugin.commons.IsScopedGraphNode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkState;

public abstract class AbstractScopedNodeSerializer implements Serializer {

    @Override
    public void serialize(FlowGraph graph, JSONArray sequence, GraphNode node, GraphNode stop) {
        checkState(IsScopedGraphNode.of(node), "Expected ScopedGraphNode");

        Optional<GraphNode> firstNodeOutsideScope = FindFirstNodeOutsideCurrentScope.of(graph, (ScopedGraphNode) node);
        GraphNode firstStop = firstNodeOutsideScope.orElse(stop);

        JSONObject serializedObject = serializeScopedNode(graph, (ScopedGraphNode) node, firstStop);
        sequence.put(serializedObject);

        firstNodeOutsideScope
                .ifPresent(n -> SerializerFactory.get()
                        .node(n)
                        .build()
                        .serialize(graph, sequence, n, stop));
    }

    protected abstract JSONObject serializeScopedNode(FlowGraph graph, ScopedGraphNode scopedNode, GraphNode stop);

}
