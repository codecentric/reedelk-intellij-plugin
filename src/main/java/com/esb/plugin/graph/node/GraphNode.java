package com.esb.plugin.graph.node;

import com.esb.plugin.editor.designer.Drawable;
import com.esb.plugin.graph.FlowGraph;

public interface GraphNode extends Drawable, ComponentAware {

    default void onSuccessorAdded(FlowGraph graph, GraphNode successor) {
    }

    default void onSuccessorAdded(FlowGraph graph, GraphNode successor, int index) {
    }

    default boolean isSuccessorAllowed(FlowGraph graph, GraphNode successor, int index) {
        return true;
    }

    default boolean isPredecessorAllowed(FlowGraph graph, GraphNode predecessor) {
        return true;
    }

}
