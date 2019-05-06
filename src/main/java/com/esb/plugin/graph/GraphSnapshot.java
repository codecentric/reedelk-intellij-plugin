package com.esb.plugin.graph;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GraphSnapshot {

    private FlowGraph graph;
    private List<SnapshotListener> listeners = new ArrayList<>();

    public void updateSnapshot(Object notifier, @NotNull FlowGraph graph) {
        this.graph = graph;
        for (SnapshotListener listener : listeners) {
            if (listener != notifier) {
                listener.onStructureChange(graph);
            }
        }
    }

    public void onDataChange() {
        for (SnapshotListener listener : listeners) {
            listener.onDataChange(graph);
        }
    }

    public void addListener(SnapshotListener listener) {
        this.listeners.add(listener);
    }

    public FlowGraph getGraph() {
        return graph;
    }
}
