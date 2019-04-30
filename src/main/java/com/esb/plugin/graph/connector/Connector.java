package com.esb.plugin.graph.connector;

import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedNode;

public interface Connector {

    void add();

    void root();

    void addToScope(ScopedNode scope);

    void addSuccessor(GraphNode successor);

    void addPredecessor(GraphNode predecessor);

    void addPredecessor(ScopedNode predecessor, int index);

}
