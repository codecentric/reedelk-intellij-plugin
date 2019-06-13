package com.esb.plugin.editor.designer.action;

import com.esb.plugin.commons.PrintFlowInfo;
import com.esb.plugin.component.domain.ComponentDescriptor;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphChangeAware;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.action.ActionNodeAdd;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.GraphNodeFactory;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static java.awt.dnd.DnDConstants.ACTION_COPY_OR_MOVE;
import static java.lang.String.format;
import static java.util.Arrays.asList;

public class DropActionHandler {

    private static final Logger LOG = Logger.getInstance(DropActionHandler.class);

    private final Module module;
    private final Graphics2D graphics;
    private final GraphSnapshot snapshot;
    private final DropTargetDropEvent dropEvent;
    private final ImageObserver imageObserver;

    public DropActionHandler(Module module, GraphSnapshot snapshot, Graphics2D graphics, DropTargetDropEvent dropEvent, ImageObserver imageObserver) {
        checkArgument(module != null, "module");
        checkArgument(snapshot != null, "snapshot");
        checkArgument(graphics != null, "graphics");
        checkArgument(dropEvent != null, "drop event");

        this.module = module;
        this.snapshot = snapshot;
        this.graphics = graphics;
        this.dropEvent = dropEvent;
        this.imageObserver = imageObserver;
    }

    public void handle() {

        Optional<ComponentDescriptor> optionalDescriptor = getComponentDescriptorFrom(dropEvent);

        if (!optionalDescriptor.isPresent()) {
            dropEvent.rejectDrop();
            return;
        }

        Point dropPoint = dropEvent.getLocation();

        ComponentDescriptor descriptor = optionalDescriptor.get();

        GraphNode nodeToAdd = GraphNodeFactory.get(descriptor);

        FlowGraph copy = snapshot.getGraph().copy();
        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(copy);

        LOG.info(format("Node Dropped [%s], drop point [x: %d, y: %d]", PrintFlowInfo.name(nodeToAdd), dropPoint.x, dropPoint.y));
        ActionNodeAdd actionNodeAdd = new ActionNodeAdd(modifiableGraph, dropPoint, nodeToAdd, graphics, imageObserver);
        actionNodeAdd.execute();

        if (modifiableGraph.isChanged()) {
            modifiableGraph.commit(module);
            dropEvent.acceptDrop(ACTION_COPY_OR_MOVE);
            snapshot.updateSnapshot(this, modifiableGraph);
        } else {
            dropEvent.rejectDrop();
        }
    }

    private Optional<ComponentDescriptor> getComponentDescriptorFrom(DropTargetDropEvent dropEvent) {
        Transferable transferable = dropEvent.getTransferable();
        DataFlavor[] transferDataFlavor = transferable.getTransferDataFlavors();
        if (asList(transferDataFlavor).contains(ComponentDescriptor.FLAVOR)) {
            try {
                ComponentDescriptor descriptor = (ComponentDescriptor) transferable.getTransferData(ComponentDescriptor.FLAVOR);
                return Optional.of(descriptor);
            } catch (UnsupportedFlavorException | IOException e) {
                LOG.error("Could not extract dropped component name", e);
            }
        }
        return Optional.empty();
    }
}
