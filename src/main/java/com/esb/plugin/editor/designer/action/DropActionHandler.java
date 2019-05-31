package com.esb.plugin.editor.designer.action;

import com.esb.plugin.component.domain.ComponentDescriptor;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphChangeAware;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.GraphNodeFactory;
import com.intellij.openapi.module.Module;

import java.awt.*;
import java.awt.dnd.DropTargetDropEvent;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static java.awt.dnd.DnDConstants.ACTION_COPY_OR_MOVE;

public class DropActionHandler extends AbstractActionHandler {

    private final Graphics2D graphics;
    private final GraphSnapshot snapshot;
    private final DropTargetDropEvent dropEvent;

    public DropActionHandler(Module module, GraphSnapshot snapshot, Graphics2D graphics, DropTargetDropEvent dropEvent) {
        super(module);
        checkArgument(snapshot != null, "snapshot");
        checkArgument(graphics != null, "graphics");
        checkArgument(dropEvent != null, "drop event");

        this.snapshot = snapshot;
        this.graphics = graphics;
        this.dropEvent = dropEvent;
    }

    public void handle() {

        Optional<ComponentDescriptor> optionalDescriptor =
                DropActionHandlerUtils.getComponentDescriptorFrom(dropEvent);

        if (!optionalDescriptor.isPresent()) {
            dropEvent.rejectDrop();
            return;
        }

        Point dropPoint = dropEvent.getLocation();

        ComponentDescriptor descriptor = optionalDescriptor.get();

        GraphNode nodeToAdd = GraphNodeFactory.get(descriptor);

        FlowGraph copy = snapshot.getGraph().copy();
        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(copy);


        addNodeToGraph(modifiableGraph, nodeToAdd, dropPoint, graphics);

        if (modifiableGraph.isChanged()) {
            modifiableGraph.commit();
            dropEvent.acceptDrop(ACTION_COPY_OR_MOVE);
            snapshot.updateSnapshot(this, modifiableGraph);
        } else {
            dropEvent.rejectDrop();
        }
    }

}
