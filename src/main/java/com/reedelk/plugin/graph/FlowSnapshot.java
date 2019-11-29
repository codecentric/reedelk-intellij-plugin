package com.reedelk.plugin.graph;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class FlowSnapshot {

    private final Set<SnapshotListener> listeners = new HashSet<>();

    private FlowGraph graph;

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

    public void addListener(@NotNull SnapshotListener listener) {
        this.listeners.add(listener);
    }


    public void applyOnValidGraph(@NotNull Consumer<FlowGraph> validGraphConsumer) {
        if (graph != null) {
            if (!graph.isError()) {
                validGraphConsumer.accept(graph);
            }
        }
    }

    public void applyOnGraph(@NotNull Consumer<FlowGraph> validGraphConsumer,
                             @NotNull Consumer<Void> emptyGraphConsumer,
                             @NotNull Consumer<ErrorFlowGraph> errorGraphConsumer) {
        if (graph == null) {
            emptyGraphConsumer.accept(null);
        } else if (graph.isError()) {
            errorGraphConsumer.accept((ErrorFlowGraph) graph);
        } else {
            validGraphConsumer.accept(graph);
        }
    }

    public FlowGraph getGraphOrThrowIfAbsent() {
        if (graph == null) {
            throw new IllegalStateException("Expected a not null graph");
        }
        return graph;
    }
}
