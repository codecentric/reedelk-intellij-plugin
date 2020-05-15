package com.reedelk.plugin.editor.designer.action;

import com.reedelk.module.descriptor.model.component.ComponentType;
import com.reedelk.plugin.graph.node.GraphNode;

public interface ActionStrategy {

    void execute(GraphNode node);

    default boolean applicableOn(GraphNode node) {
        // By default a strategy can only be applied
        // on components which are not inbound.
        // For Inbound components a strategy needs to replace this method.
        return !ComponentType.INBOUND.equals(node.getComponentType());
    }
}
