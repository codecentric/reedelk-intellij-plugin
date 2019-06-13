package com.esb.plugin.graph;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FlowSnapshot {

    private FlowGraph graph;

    private final FlowGraphProvider graphProvider;
    private final List<SnapshotListener> listeners = new ArrayList<>();

    public FlowSnapshot(FlowGraphProvider graphProvider) {
        this.graphProvider = graphProvider;
    }

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

    @NotNull
    public FlowGraph getGraph() {
        return graph != null ?
                graph :
                graphProvider.createGraph();
    }
}
