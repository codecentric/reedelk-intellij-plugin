package com.esb.plugin.service.project;

import com.esb.plugin.graph.FlowSnapshot;
import org.jetbrains.annotations.NotNull;

public class SelectableItemSubflow implements SelectableItem {

    private final FlowSnapshot snapshot;

    public SelectableItemSubflow(@NotNull FlowSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    @Override
    public FlowSnapshot getSnapshot() {
        return snapshot;
    }
}
