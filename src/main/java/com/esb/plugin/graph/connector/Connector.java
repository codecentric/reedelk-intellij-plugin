package com.esb.plugin.graph.connector;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;

public interface Connector {

    void add();

    void root();

    void addToScope(ScopedGraphNode scope);

    void addSuccessor(GraphNode successor);

    void addPredecessor(GraphNode predecessor);

    void addPredecessor(ScopedGraphNode predecessor, int index);

    default boolean isPredecessorAllowed(FlowGraph graph, ScopedGraphNode node, int index) {
        return true;
    }

}
