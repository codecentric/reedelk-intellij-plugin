package com.esb.plugin.graph.node;

import com.esb.plugin.graph.FlowGraph;
import com.intellij.openapi.module.Module;

public interface GraphOperationListener {

    default boolean isSuccessorAllowed(FlowGraph graph, GraphNode successor, int index) {
        return true;
    }

    /**
     * Called when all the changes have been made.
     */
    default void commit(FlowGraph graph, Module module) {

    }
}
