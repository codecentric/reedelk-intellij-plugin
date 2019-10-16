package com.reedelk.plugin.editor.designer;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.reedelk.plugin.commons.DefaultDescriptorDataValuesFiller;
import com.reedelk.plugin.commons.PrintFlowInfo;
import com.reedelk.plugin.component.domain.ComponentData;
import com.reedelk.plugin.component.domain.ComponentDescriptor;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.editor.designer.action.DropActionHandler;
import com.reedelk.plugin.editor.designer.action.MoveActionHandler;
import com.reedelk.plugin.editor.designer.action.RemoveActionHandler;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.action.Action;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.GraphNodeFactory;
import com.reedelk.runtime.component.Placeholder;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.reedelk.plugin.editor.palette.ComponentDescriptorTransferable.FLAVOR;
import static java.awt.dnd.DnDConstants.ACTION_COPY_OR_MOVE;
import static java.lang.String.format;
import static java.util.Arrays.asList;

public abstract class AbstractDesignerPanelActionHandler implements DesignerPanelActionHandler {

    private static final Logger LOG = Logger.getInstance(AbstractDesignerPanelActionHandler.class);

    protected final FlowSnapshot snapshot;
    protected final Module module;

    protected AbstractDesignerPanelActionHandler(@NotNull Module module,
                                                 @NotNull FlowSnapshot snapshot) {
        this.snapshot = snapshot;
        this.module = module;
    }

    @Override
    public void onMove(Graphics2D graphics, GraphNode selected, Point dragPoint, ImageObserver observer) {
        Point dropPoint = new Point(dragPoint.x, dragPoint.y);
        GraphNode placeHolderNode = GraphNodeFactory.get(module, Placeholder.class.getName());

        Action actionRemove = getActionRemove(placeHolderNode);
        Action actionReplace = getActionReplace(selected, placeHolderNode);
        Action actionAdd = getActionAdd(selected, dropPoint, graphics, observer);

        MoveActionHandler handler = MoveActionHandler.builder()
                .module(module)
                .snapshot(snapshot)
                .graphics(graphics)
                .movedNode(selected)
                .movePoint(dragPoint)
                .actionAdd(actionAdd)
                .actionRemove(actionRemove)
                .actionReplace(actionReplace)
                .replacementNode(placeHolderNode)
                .build();

        handler.handle();
    }

    @Override
    public void onRemove(GraphNode nodeToRemove) {
        Action actionRemove = getActionRemove(nodeToRemove);
        RemoveActionHandler handler =
                new RemoveActionHandler(module, snapshot, actionRemove);
        handler.handle();
    }

    @Override
    public Optional<GraphNode> onAdd(Graphics2D graphics, DropTargetDropEvent dropEvent, ImageObserver observer) {

        Point dropPoint = dropEvent.getLocation();

        Optional<ComponentDescriptor> optionalDescriptor = getComponentDescriptorFrom(dropEvent);

        if (optionalDescriptor.isPresent()) {

            ComponentDescriptor descriptor = optionalDescriptor.get();

            GraphNode nodeToAdd = GraphNodeFactory.get(descriptor);

            ComponentData componentData = nodeToAdd.componentData();

            List<ComponentPropertyDescriptor> propertiesDescriptors = componentData.getPropertiesDescriptors();

            // Fill default property values for the just added component
            DefaultDescriptorDataValuesFiller.fill(componentData, propertiesDescriptors);

            LOG.info(format("Node Dropped [%s], drop point [x: %d, y: %d]", PrintFlowInfo.name(nodeToAdd), dropPoint.x, dropPoint.y));

            Action addAction = getActionAdd(nodeToAdd, dropPoint, graphics, observer);

            DropActionHandler handler = new DropActionHandler(module, snapshot, addAction);

            boolean handled = handler.handle();

            if (handled) {

                dropEvent.acceptDrop(ACTION_COPY_OR_MOVE);

                return Optional.of(nodeToAdd);
            }

        }

        dropEvent.rejectDrop();

        return Optional.empty();
    }

    protected abstract Action getActionAdd(GraphNode nodeToAdd, Point dropPoint, Graphics2D graphics, ImageObserver observer);

    protected abstract Action getActionRemove(GraphNode nodeToRemove);

    protected abstract Action getActionReplace(GraphNode nodeFrom, GraphNode nodeTo);

    private Optional<ComponentDescriptor> getComponentDescriptorFrom(DropTargetDropEvent dropEvent) {
        Transferable transferable = dropEvent.getTransferable();
        DataFlavor[] transferDataFlavor = transferable.getTransferDataFlavors();

        if (asList(transferDataFlavor).contains(FLAVOR)) {
            try {
                ComponentDescriptor descriptor =
                        (ComponentDescriptor) transferable.getTransferData(FLAVOR);
                return Optional.of(descriptor);
            } catch (UnsupportedFlavorException | IOException e) {
                LOG.error("Could not extract dropped component name", e);
            }
        }
        return Optional.empty();
    }
}