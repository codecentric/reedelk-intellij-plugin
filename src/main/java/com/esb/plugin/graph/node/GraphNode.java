package com.esb.plugin.graph.node;

public interface GraphNode extends Drawable, ComponentAware {

    default void onSuccessorAdded(GraphNode successor) {
    }

    default void onSuccessorRemoved(GraphNode successor) {
    }

}
