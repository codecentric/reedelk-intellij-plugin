package com.esb.plugin.editor.designer.action;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphChangeAware;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.action.Action;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

public class DropActionHandler {

    private final FlowSnapshot snapshot;
    private final Action addAction;
    private final Module module;

    public DropActionHandler(@NotNull Module module,
                             @NotNull FlowSnapshot snapshot,
                             @NotNull Action addAction) {
        this.addAction = addAction;
        this.snapshot = snapshot;
        this.module = module;
    }

    public boolean handle() {
        FlowGraph copy = snapshot.getGraphOrThrowIfAbsent();

        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(copy);

        addAction.execute(modifiableGraph);

        if (modifiableGraph.isChanged()) {

            modifiableGraph.commit(module);

            snapshot.updateSnapshot(this, modifiableGraph);

            return true;

        }

        return false;
    }
}
