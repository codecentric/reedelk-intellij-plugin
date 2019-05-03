package com.esb.plugin.designer.canvas.action;

import com.esb.plugin.component.ComponentDescriptor;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphChangeAware;
import com.esb.plugin.graph.FlowGraphImpl;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.GraphNodeFactory;
import com.intellij.openapi.module.Module;

import java.awt.*;
import java.awt.dnd.DropTargetDropEvent;
import java.util.Optional;

import static java.awt.dnd.DnDConstants.ACTION_COPY_OR_MOVE;

public class DropActionHandler extends AbstractActionHandler {

    private final FlowGraph graph;
    private final Graphics2D graphics;
    private final DropTargetDropEvent dropEvent;

    public DropActionHandler(Module module, FlowGraph graph, Graphics2D graphics, DropTargetDropEvent dropEvent) {
        super(module);
        this.graph = graph;
        this.graphics = graphics;
        this.dropEvent = dropEvent;
    }

    public Optional<FlowGraph> handle() {

        Optional<ComponentDescriptor> optionalDescriptor =
                DropActionHandlerUtils.getComponentDescriptorFrom(dropEvent);

        if (!optionalDescriptor.isPresent()) {
            dropEvent.rejectDrop();
            return Optional.empty();
        }

        Point dropPoint = dropEvent.getLocation();

        ComponentDescriptor descriptor = optionalDescriptor.get();

        FlowGraph copy = graph == null ? new FlowGraphImpl() : graph.copy();

        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(copy);

        GraphNode componentToAdd = GraphNodeFactory.get(descriptor);

        addDrawableToGraph(modifiableGraph, componentToAdd, dropPoint, graphics);

        if (modifiableGraph.isChanged()) {
            dropEvent.acceptDrop(ACTION_COPY_OR_MOVE);
            return Optional.of(modifiableGraph);
        }

        dropEvent.rejectDrop();
        return Optional.empty();
    }

}
