package com.esb.plugin.designer.graph;

import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.drawable.ChoiceDrawable;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.DrawableFactory;
import com.esb.plugin.designer.graph.drawable.FlowReferenceDrawable;

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
        Optional<String> optionalComponentName = getComponentName(dropEvent);
        if (!optionalComponentName.isPresent()) {
            dropEvent.rejectDrop();
            return Optional.empty();
        }

        Point dropPoint = dropEvent.getLocation();
        String componentName = optionalComponentName.get();


        if (graph == null) {
            graph = new FlowGraphImpl();
        }
        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(graph.copy());
        Connector connector = createComponentConnector(componentName, modifiableGraph);

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

    private Optional<String> getComponentName(DropTargetDropEvent dropEvent) {
        Transferable transferable = dropEvent.getTransferable();
        DataFlavor[] transferDataFlavor = transferable.getTransferDataFlavors();
        if (asList(transferDataFlavor).contains(stringFlavor)) {
            try {
                String componentName = (String) transferable.getTransferData(stringFlavor);
                return Optional.of(componentName);
            } catch (UnsupportedFlavorException | IOException e) {
                // TODO: Log this exception
            }
        }
        return Optional.empty();
    }

    private Connector createComponentConnector(String componentName, FlowGraph graph) {
        Drawable componentToAdd = DrawableFactory.get(componentName);

        if (componentToAdd instanceof ChoiceDrawable) {
            FlowGraph choiceGraph = new FlowGraphImpl();
            choiceGraph.root(componentToAdd);
            FlowReferenceDrawable placeholderDrawable = new FlowReferenceDrawable(new Component("Flow ref"));
            choiceGraph.add(componentToAdd, placeholderDrawable);
            ((ChoiceDrawable) componentToAdd).addToScope(placeholderDrawable);
            return new ChoiceConnector(graph, choiceGraph);
        }
        return new DrawableConnector(graph, componentToAdd);
    }
}
