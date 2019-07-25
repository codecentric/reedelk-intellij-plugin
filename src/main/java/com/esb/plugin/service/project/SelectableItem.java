package com.esb.plugin.service.project;

import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.openapi.module.Module;

/**
 * A selection item could be a component, just a flow or nothing.
 */
public interface SelectableItem {

    default Module getModule() {
        throw new UnsupportedOperationException();
    }

    default FlowSnapshot getSnapshot() {
        throw new UnsupportedOperationException();
    }

    default GraphNode getSelectedNode() {
        throw new UnsupportedOperationException();
    }

}
