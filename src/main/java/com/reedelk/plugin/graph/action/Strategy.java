package com.reedelk.plugin.graph.action;

import com.reedelk.plugin.graph.node.GraphNode;

import static com.reedelk.plugin.component.domain.ComponentClass.INBOUND;

public interface Strategy {

    void execute(GraphNode node);

    default boolean applicableOn(GraphNode node) {
        // By default a strategy can only be applied
        // on components which are not inbound.
        // For Inbound components a strategy needs to replace this method.
        return !INBOUND.equals(node.getComponentClass());
    }

}
