package com.reedelk.plugin.graph.node;

import com.reedelk.plugin.editor.designer.action.remove.strategy.PlaceholderProvider;
import com.reedelk.plugin.graph.FlowGraph;

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
     * Called on a ScopedGraphNode or a GraphNode when it has been added to the graph.
     */
    default void onAdded(FlowGraph graph, PlaceholderProvider placeholderProvider) {
    }

    /*
     * This is an event called only when a successor is added to GraphNode.
     */
    default void onSuccessorAdded(FlowGraph graph, GraphNode addedNode) {
    }

    /**
     * * This is an event called only when a successor is added to GraphNode.
     */
    default void onSuccessorAdded(FlowGraph graph, GraphNode addedNode, int index) {
    }

    /*
     * This is an event called only when a successor is removed from a GraphNode.
     */
    default void onSuccessorRemoved(FlowGraph graph, GraphNode removedNode, PlaceholderProvider placeholderProvider) {
    }

    /**
     * This is an event called only when a successor is removed from a ScopedGraphNode.
     */
    default void onSuccessorRemoved(FlowGraph graph, GraphNode removedNode, int index, PlaceholderProvider placeholderProvider) {
    }
}
