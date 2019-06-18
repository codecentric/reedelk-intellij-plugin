package com.esb.plugin.editor.designer;

import com.esb.plugin.commons.PrintFlowInfo;
import com.esb.plugin.component.domain.ComponentDescriptor;
import com.esb.plugin.editor.designer.action.DropActionHandler;
import com.esb.plugin.editor.designer.action.MoveActionHandler;
import com.esb.plugin.editor.designer.action.RemoveActionHandler;
import com.esb.plugin.graph.FlowSnapshot;
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

import static com.esb.plugin.component.domain.ComponentDescriptor.FLAVOR;
import static java.lang.String.format;
import static java.util.Arrays.asList;

public abstract class AbstractDesignerPanelActionHandler implements DesignerPanelActionHandler {

    private static final Logger LOG = Logger.getInstance(AbstractDesignerPanelActionHandler.class);

    protected final FlowSnapshot snapshot;
    protected final Module module;

    protected AbstractDesignerPanelActionHandler(Module module, FlowSnapshot snapshot) {
        this.snapshot = snapshot;
        this.module = module;
    }

    @Override
    public void onMove(Graphics2D graphics, GraphNode selected, Point dragPoint, ImageObserver observer) {
        Point dropPoint = new Point(dragPoint.x, dragPoint.y);
        ActionNodeAdd actionNodeAdd = getActionNodeAdd(dropPoint, selected, graphics, observer);

        MoveActionHandler moveActionHandler =
                new MoveActionHandler(module, snapshot, graphics, selected, dragPoint, actionNodeAdd);

        moveActionHandler.handle();
    }

    @Override
    public void onRemove(GraphNode nodeToRemove) {
        RemoveActionHandler handler =
                new RemoveActionHandler(module, snapshot, nodeToRemove);
        handler.handle();
    }

    @Override
    public void onDrop(Graphics2D graphics, DropTargetDropEvent dropEvent, ImageObserver observer) {
        // The arguments should be drop point and the node to add!
        Point dropPoint = dropEvent.getLocation();
        Optional<ComponentDescriptor> optionalDescriptor = getComponentDescriptorFrom(dropEvent);
        if (optionalDescriptor.isPresent()) {

            ComponentDescriptor descriptor = optionalDescriptor.get();

            GraphNode nodeToAdd = GraphNodeFactory.get(descriptor);

            // Fill default property values for the just added component
            DefaultDescriptorDataValuesFiller.fill(nodeToAdd);

            LOG.info(format("Node Dropped [%s], drop point [x: %d, y: %d]", PrintFlowInfo.name(nodeToAdd), dropPoint.x, dropPoint.y));

            ActionNodeAdd actionNodeAdd = getActionNodeAdd(dropPoint, nodeToAdd, graphics, observer);

            DropActionHandler handler =
                    new DropActionHandler(module, snapshot, dropEvent, actionNodeAdd);
            handler.handle();

        } else {

            dropEvent.rejectDrop();

        }
    }

    protected abstract ActionNodeAdd getActionNodeAdd(Point dropPoint, GraphNode nodeToAdd, Graphics2D graphics, ImageObserver observer);

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
