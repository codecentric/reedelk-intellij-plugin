package com.reedelk.plugin.editor.designer.action;

import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.FlowGraphChangeAware;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.action.Action;
import com.reedelk.plugin.graph.action.remove.strategy.PlaceholderProvider;
import org.jetbrains.annotations.NotNull;

public class AddActionHandler {

    private final PlaceholderProvider placeholderProvider;
    private final FlowSnapshot snapshot;
    private final Action addAction;

    public AddActionHandler(@NotNull PlaceholderProvider placeholderProvider,
                            @NotNull FlowSnapshot snapshot,
                            @NotNull Action addAction) {
        this.placeholderProvider = placeholderProvider;
        this.addAction = addAction;
        this.snapshot = snapshot;
    }

    public boolean handle() {
        FlowGraph copy = snapshot.getGraphOrThrowIfAbsent();

        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(copy);

        addAction.execute(modifiableGraph);

        if (modifiableGraph.isChanged()) {

            modifiableGraph.commit(placeholderProvider);

            snapshot.updateSnapshot(this, modifiableGraph);

            return true;

        }

        return false;
    }
}
