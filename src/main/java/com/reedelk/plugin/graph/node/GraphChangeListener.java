package com.reedelk.plugin.graph.node;

import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.action.remove.strategy.PlaceholderProvider;

public interface GraphChangeListener {

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
    default void onNodeAdded(FlowGraph graph, PlaceholderProvider placeholderProvider) {
    }

    // Called when a successor of a scoped node has been removed from the given index
    default void onSuccessorRemoved(PlaceholderProvider placeholderProvider, FlowGraph graph, GraphNode removedNode, int index) {
    }
}
