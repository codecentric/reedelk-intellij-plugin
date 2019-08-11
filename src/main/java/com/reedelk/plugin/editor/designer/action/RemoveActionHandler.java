package com.reedelk.plugin.editor.designer.action;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.FlowGraphChangeAware;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.action.Action;
import org.jetbrains.annotations.NotNull;

public class RemoveActionHandler {

    private final FlowSnapshot snapshot;
    private final Action removeAction;
    private final Module module;

    public RemoveActionHandler(@NotNull Module module,
                               @NotNull FlowSnapshot snapshot,
                               @NotNull Action removeAction) {
        this.removeAction = removeAction;
        this.snapshot = snapshot;
        this.module = module;
    }

    public void handle() {
        FlowGraph graph = snapshot.getGraphOrThrowIfAbsent();

        FlowGraph copy = graph.copy();

        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(copy);

        removeAction.execute(modifiableGraph);

        if (modifiableGraph.isChanged()) {

            modifiableGraph.commit(module);

            snapshot.updateSnapshot(this, modifiableGraph);
        }
    }
}
