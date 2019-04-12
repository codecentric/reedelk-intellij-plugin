package com.esb.plugin.designer.editor.designer;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.FlowGraphChangeAware;
import com.esb.plugin.designer.graph.FlowGraphImpl;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.DrawableFactory;
import com.intellij.openapi.diagnostic.Logger;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTargetDropEvent;
import java.io.IOException;
import java.util.Optional;

import static java.awt.datatransfer.DataFlavor.stringFlavor;
import static java.awt.dnd.DnDConstants.ACTION_COPY_OR_MOVE;
import static java.util.Arrays.asList;

class PaletteDropActionHandler extends AbstractActionHandler {

    private static final Logger LOG = Logger.getInstance(PaletteDropActionHandler.class);

    private final FlowGraph graph;
    private final Graphics2D graphics;
    private final DropTargetDropEvent dropEvent;

    PaletteDropActionHandler(FlowGraph graph, Graphics2D graphics, DropTargetDropEvent dropEvent) {
        this.graph = graph;
        this.graphics = graphics;
        this.dropEvent = dropEvent;
    }

    Optional<FlowGraph> handle() {
        Optional<String> optionalComponentName = extractComponentName(dropEvent);
        if (!optionalComponentName.isPresent()) {
            dropEvent.rejectDrop();
            return Optional.empty();
        }

        Point dropPoint = dropEvent.getLocation();
        String componentName = optionalComponentName.get();

        FlowGraph copy = graph == null ? new FlowGraphImpl() : graph.copy();

        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(copy);
        Drawable componentToAdd = DrawableFactory.get(componentName);
        addDrawableToGraph(modifiableGraph, componentToAdd, dropPoint, graphics);

        if (modifiableGraph.isChanged()) {
            dropEvent.acceptDrop(ACTION_COPY_OR_MOVE);
            return Optional.of(modifiableGraph);
        } else {
            dropEvent.rejectDrop();
        }

        return Optional.empty();
    }

    private Optional<String> extractComponentName(DropTargetDropEvent dropEvent) {
        Transferable transferable = dropEvent.getTransferable();
        DataFlavor[] transferDataFlavor = transferable.getTransferDataFlavors();
        if (asList(transferDataFlavor).contains(stringFlavor)) {
            try {
                String componentName = (String) transferable.getTransferData(stringFlavor);
                return Optional.of(componentName);
            } catch (UnsupportedFlavorException | IOException e) {
                LOG.error("Could not extract dropped component name", e);
            }
        }
        return Optional.empty();
    }

}
