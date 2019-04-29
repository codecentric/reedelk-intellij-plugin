package com.esb.plugin.designer.graph.connector;

import com.esb.plugin.designer.graph.GraphNode;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;

public interface Connector {

    void add();

    void root();

    void addToScope(ScopedDrawable scope);

    void addSuccessor(GraphNode successor);

    void addPredecessor(GraphNode predecessor);

    void addPredecessor(ScopedDrawable predecessor, int index);

}
