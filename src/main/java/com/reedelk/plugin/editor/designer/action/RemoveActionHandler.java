package com.reedelk.plugin.editor.designer.action;

import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.FlowGraphChangeAware;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.action.Action;
import com.reedelk.plugin.graph.action.remove.strategy.PlaceholderProvider;
import org.jetbrains.annotations.NotNull;

public class RemoveActionHandler {

    private final PlaceholderProvider placeholderProvider;
    private final FlowSnapshot snapshot;
    private final Action removeAction;

    public RemoveActionHandler(@NotNull PlaceholderProvider placeholderProvider,
                               @NotNull FlowSnapshot snapshot,
                               @NotNull Action removeAction) {
        this.placeholderProvider = placeholderProvider;
        this.removeAction = removeAction;
        this.snapshot = snapshot;
    }

    public void handle() {
        FlowGraph graph = snapshot.getGraphOrThrowIfAbsent();

        FlowGraph copy = graph.copy();

        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(copy);

        removeAction.execute(modifiableGraph);

        if (modifiableGraph.isChanged()) {

            modifiableGraph.commit(placeholderProvider);

            snapshot.updateSnapshot(this, modifiableGraph);
        }
    }
}
