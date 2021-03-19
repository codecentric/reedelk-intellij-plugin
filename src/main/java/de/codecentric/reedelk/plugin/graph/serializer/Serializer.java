package de.codecentric.reedelk.plugin.graph.serializer;

import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import org.json.JSONArray;

public interface Serializer {

    default void serialize(FlowGraph graph, JSONArray sequence, GraphNode node, GraphNode stop) {
        throw new UnsupportedOperationException();
    }
}
