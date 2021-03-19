package de.codecentric.reedelk.plugin.editor.properties.selection;

import de.codecentric.reedelk.plugin.graph.FlowSnapshot;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
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