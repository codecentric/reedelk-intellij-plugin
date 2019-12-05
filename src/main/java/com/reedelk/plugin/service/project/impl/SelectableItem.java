package com.reedelk.plugin.service.project.impl;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.node.GraphNode;

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