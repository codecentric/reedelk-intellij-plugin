package com.esb.plugin.editor.designer.action;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphChangeAware;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.action.ActionNodeAdd;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;

import java.awt.dnd.DropTargetDropEvent;

import static com.google.common.base.Preconditions.checkArgument;
import static java.awt.dnd.DnDConstants.ACTION_COPY_OR_MOVE;

public class DropActionHandler {

    private static final Logger LOG = Logger.getInstance(DropActionHandler.class);

    private final Module module;
    private final GraphSnapshot snapshot;
    private final DropTargetDropEvent dropEvent;
    private final ActionNodeAdd actionNodeAdd;

    public DropActionHandler(Module module, GraphSnapshot snapshot, DropTargetDropEvent dropEvent, ActionNodeAdd actionNodeAdd) {
        checkArgument(module != null, "module");
        checkArgument(snapshot != null, "snapshot");
        checkArgument(dropEvent != null, "drop event");

        this.module = module;
        this.snapshot = snapshot;
        this.dropEvent = dropEvent;
        this.actionNodeAdd = actionNodeAdd;
    }

    public void handle() {
        FlowGraph copy = snapshot.getGraph().copy();
        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(copy);

        actionNodeAdd.execute(modifiableGraph);

        if (modifiableGraph.isChanged()) {
            modifiableGraph.commit(module);
            dropEvent.acceptDrop(ACTION_COPY_OR_MOVE);
            snapshot.updateSnapshot(this, modifiableGraph);
        } else {
            dropEvent.rejectDrop();
        }
    }
}
