package com.reedelk.plugin.graph.node;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.graph.FlowGraph;

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
