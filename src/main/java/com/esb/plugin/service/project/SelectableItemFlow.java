package com.esb.plugin.service.project;

import com.esb.plugin.graph.FlowSnapshot;

public class SelectableItemFlow implements SelectableItem {

    private final FlowSnapshot snapshot;

    public SelectableItemFlow(FlowSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    @Override
    public FlowSnapshot getSnapshot() {
        return snapshot;
    }
}
