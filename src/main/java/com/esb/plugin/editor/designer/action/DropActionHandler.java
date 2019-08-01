package com.esb.plugin.editor.designer.action;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphChangeAware;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.action.Action;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

import java.awt.dnd.DropTargetDropEvent;

import static java.awt.dnd.DnDConstants.ACTION_COPY_OR_MOVE;

public class DropActionHandler {

    private final DropTargetDropEvent dropEvent;
    private final FlowSnapshot snapshot;
    private final Action addAction;
    private final Module module;

    public DropActionHandler(@NotNull Module module,
                             @NotNull FlowSnapshot snapshot,
                             @NotNull DropTargetDropEvent dropEvent,
                             @NotNull Action addAction) {
        this.addAction = addAction;
        this.dropEvent = dropEvent;
        this.snapshot = snapshot;
        this.module = module;
    }

    public void handle() {
        FlowGraph copy = snapshot.getGraphOrThrowIfAbsent();

        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(copy);

        addAction.execute(modifiableGraph);

        if (modifiableGraph.isChanged()) {

            modifiableGraph.commit(module);

            dropEvent.acceptDrop(ACTION_COPY_OR_MOVE);

            snapshot.updateSnapshot(this, modifiableGraph);

        } else {

            dropEvent.rejectDrop();

        }
    }
}
