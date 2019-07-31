package com.esb.plugin.editor.designer;

import com.esb.plugin.commons.DefaultDescriptorDataValuesFiller;
import com.esb.plugin.commons.PrintFlowInfo;
import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.component.domain.ComponentDescriptor;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.editor.designer.action.DropActionHandler;
import com.esb.plugin.editor.designer.action.MoveActionHandler;
import com.esb.plugin.editor.designer.action.RemoveActionHandler;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.action.Action;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.GraphNodeFactory;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
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

import static com.esb.plugin.editor.palette.ComponentDescriptorTransferable.FLAVOR;
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
        Action addAction = getAddAction(dropPoint, selected, graphics, observer);
        Action actionRemove = getRemoveAction(selected);

        MoveActionHandler handler = new MoveActionHandler(
                module,
                snapshot,
                graphics,
                selected,
                dragPoint,
                addAction,
                actionRemove);

        handler.handle();
    }

    @Override
    public void onRemove(GraphNode nodeToRemove) {
        Action removeAction = getRemoveAction(nodeToRemove);
        RemoveActionHandler handler =
                new RemoveActionHandler(module, snapshot, removeAction);
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

            Action addAction = getAddAction(dropPoint, nodeToAdd, graphics, observer);

            DropActionHandler handler =
                    new DropActionHandler(module, snapshot, dropEvent, addAction);

            handler.handle();

            return Optional.of(nodeToAdd);

        } else {

            dropEvent.rejectDrop();

            return Optional.empty();

        }
    }

    protected abstract Action getAddAction(Point dropPoint, GraphNode nodeToAdd, Graphics2D graphics, ImageObserver observer);

    protected abstract Action getRemoveAction(GraphNode nodeToRemove);

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