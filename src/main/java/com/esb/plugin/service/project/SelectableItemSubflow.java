package com.esb.plugin.service.project;

import com.esb.plugin.graph.FlowSnapshot;

public class SelectableItemSubflow implements SelectableItem {

    private final FlowSnapshot snapshot;

    public SelectableItemSubflow(FlowSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    @Override
    public FlowSnapshot getSnapshot() {
        return snapshot;
    }
}
