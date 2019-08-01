package com.esb.plugin.editor.designer.action;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphChangeAware;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.action.Action;
import com.intellij.openapi.module.Module;
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
