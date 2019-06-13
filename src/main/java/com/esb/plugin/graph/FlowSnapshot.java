package com.esb.plugin.graph;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FlowSnapshot {

    private final String defaultTitle;
    private final String defaultDescription;

    private final FlowGraphProvider provider;
    private final List<SnapshotListener> listeners = new ArrayList<>();

    private FlowGraph graph;

    public FlowSnapshot(FlowGraphProvider provider, String defaultTitle, String defaultDescription) {
        this.provider = provider;
        this.defaultTitle = defaultTitle;
        this.defaultDescription = defaultDescription;
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
        if (graph == null) {
            graph = provider.createGraph();
            graph.setTitle(defaultTitle);
            graph.setDescription(defaultDescription);
        }
        return graph;
    }
}
