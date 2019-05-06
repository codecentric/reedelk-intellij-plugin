package com.esb.plugin.designer.canvas.action;

import com.esb.plugin.component.ComponentDescriptor;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphChangeAware;
import com.esb.plugin.graph.FlowGraphImpl;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.GraphNodeFactory;
import com.intellij.openapi.module.Module;

import java.awt.*;
import java.awt.dnd.DropTargetDropEvent;
import java.util.Optional;

import static java.awt.dnd.DnDConstants.ACTION_COPY_OR_MOVE;

public class DropActionHandler extends AbstractActionHandler {

    private final Graphics2D graphics;
    private final GraphSnapshot snapshot;
    private final DropTargetDropEvent dropEvent;

    public DropActionHandler(Module module, GraphSnapshot snapshot, Graphics2D graphics, DropTargetDropEvent dropEvent) {
        super(module);
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

        FlowGraph copy = snapshot.getGraph() == null ? new FlowGraphImpl() : snapshot.getGraph().copy();

        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(copy);

        GraphNode nodeToAdd = GraphNodeFactory.get(descriptor);

        addNodeToGraph(modifiableGraph, nodeToAdd, dropPoint, graphics);


        if (modifiableGraph.isChanged()) {
            dropEvent.acceptDrop(ACTION_COPY_OR_MOVE);
            snapshot.updateSnapshot(modifiableGraph);
        } else {
            dropEvent.rejectDrop();
        }
    }

}
