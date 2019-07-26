package com.esb.plugin.graph;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class FlowSnapshot {

    private final String defaultTitle;
    private final String defaultDescription;
    private final FlowGraphProvider provider;

    private final Set<SnapshotListener> listeners = new HashSet<>();

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
                listener.onDataChange();
            }
        }

    }

    public void onDataChange() {
        for (SnapshotListener listener : listeners) {
            listener.onDataChange();
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
