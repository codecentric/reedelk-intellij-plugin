package com.reedelk.plugin.graph.node;

import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.action.remove.strategy.PlaceholderProvider;

public interface GraphOperationListener {

    default boolean isSuccessorAllowedTop(FlowGraph graph, GraphNode successor, int index) {
        return true;
    }

    default boolean isSuccessorAllowedBottom(FlowGraph graph, GraphNode successor, int index) {
        return true;
    }

    default boolean isSuccessorAllowedBefore(FlowGraph graph, GraphNode successor, int index) {
        return true;
    }

    /**
     * Called when all the changes have been made.
     */
    default void commit(FlowGraph graph, PlaceholderProvider placeholderProvider) {
    }

    default void onRemoved(PlaceholderProvider placeholderProvider, FlowGraph graph, GraphNode removedNode, int index) {
    }
}
