package com.reedelk.plugin.editor.properties.selection;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.node.GraphNode;
import org.jetbrains.annotations.NotNull;

public class SelectableItemComponent implements SelectableItem {

    private final FlowSnapshot snapshot;
    private final GraphNode selected;
    private final Module module;

    public SelectableItemComponent(@NotNull Module module,
                                   @NotNull FlowSnapshot snapshot,
                                   @NotNull GraphNode selected) {
        this.snapshot = snapshot;
        this.selected = selected;
        this.module = module;
    }

    @Override
    public Module getModule() {
        return module;
    }

    @Override
    public FlowSnapshot getSnapshot() {
        return snapshot;
    }

    @Override
    public GraphNode getSelectedNode() {
        return selected;
    }
}
