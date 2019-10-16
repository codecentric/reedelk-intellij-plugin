package com.reedelk.plugin.editor.designer.action;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.FlowGraphChangeAware;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.action.Action;
import org.jetbrains.annotations.NotNull;

public class AddActionHandler {

    private final FlowSnapshot snapshot;
    private final Action addAction;
    private final Module module;

    public AddActionHandler(@NotNull Module module,
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
