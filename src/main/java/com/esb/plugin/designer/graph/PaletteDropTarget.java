package com.esb.plugin.designer.graph;

import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.DrawableFactory;

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

/* *
 *
 * Called when we drop a component from the PALETTE
 */
public class PaletteDropTarget {

    /**
     * Return an empty optional if the component could not be added to the graph.
     */
    public Optional<FlowGraph> drop(DropTargetDropEvent dropEvent, FlowGraph graph) {

        Transferable transferable = dropEvent.getTransferable();

        DataFlavor[] transferDataFlavor = transferable.getTransferDataFlavors();
        if (!asList(transferDataFlavor).contains(stringFlavor)) {
            dropEvent.rejectDrop();
            return Optional.empty();
        }

        String componentName;
        try {
            componentName = (String) transferable.getTransferData(stringFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            dropEvent.rejectDrop();
            return Optional.empty();
        }

        if (graph == null) {
            graph = new FlowGraphImpl();
        }

        Point dropPoint = dropEvent.getLocation();
        Drawable componentToAdd = DrawableFactory.get(componentName);

        // TODO: The component to add might be a scoped drawable.

        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(graph.copy());

        Connector connector = new DrawableConnector(modifiableGraph, componentToAdd);
        // TODO: Create a builder here
        AddDrawableToGraph nodeAdder = new AddDrawableToGraph(modifiableGraph, dropPoint, connector);
        nodeAdder.add();

        if (modifiableGraph.isChanged()) {
            graph = modifiableGraph;
            dropEvent.acceptDrop(ACTION_COPY_OR_MOVE);
            return Optional.of(graph);
        }

        dropEvent.rejectDrop();
        return Optional.empty();
    }
}
