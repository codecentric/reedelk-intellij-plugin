package com.reedelk.plugin.editor.designer.action;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.reedelk.component.descriptor.ComponentDescriptor;
import com.reedelk.component.descriptor.ComponentPropertyDescriptor;
import com.reedelk.plugin.commons.DefaultDescriptorDataValuesFiller;
import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.editor.designer.action.add.AddActionHandler;
import com.reedelk.plugin.editor.designer.action.move.MoveActionHandler;
import com.reedelk.plugin.editor.designer.action.remove.RemoveActionHandler;
import com.reedelk.plugin.editor.designer.action.remove.strategy.DefaultPlaceholderProvider;
import com.reedelk.plugin.editor.designer.action.remove.strategy.PlaceholderProvider;
import com.reedelk.plugin.editor.designer.debug.PrintFlowInfo;
import com.reedelk.plugin.editor.designer.dnd.DesignerPanelActionHandler;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.GraphNodeFactory;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.reedelk.plugin.editor.designer.action.move.MoveActionHandler.builder;
import static com.reedelk.plugin.editor.palette.ComponentDescriptorTransferable.FLAVOR;
import static java.lang.String.format;
import static java.util.Arrays.asList;

public abstract class DesignerPanelAbstractActionHandler implements DesignerPanelActionHandler {

    private static final Logger LOG = Logger.getInstance(DesignerPanelAbstractActionHandler.class);

    protected final PlaceholderProvider placeholderProvider;
    protected final FlowSnapshot snapshot;
    protected final Module module;

    DesignerPanelAbstractActionHandler(@NotNull Module module,
                                       @NotNull FlowSnapshot snapshot) {
        this.placeholderProvider = new DefaultPlaceholderProvider(module);
        this.snapshot = snapshot;
        this.module = module;
    }

    @Override
    public void onMove(Graphics2D graphics, GraphNode selected, Point dropPoint, ImageObserver observer) {
        GraphNode placeholderNode = placeholderProvider.get()
                .orElseThrow(() -> new IllegalStateException("Expected Placeholder to provide a not empty placeholder"));

        DimensionAwareGraphNodeDecorator placeHolderNode =
                new DimensionAwareGraphNodeDecorator(placeholderNode, selected);

        Action actionRemove = getActionRemove(placeHolderNode);
        Action actionReplace = getActionReplace(selected, placeHolderNode);
        Action actionAdd = getActionAdd(selected, dropPoint, graphics, observer);

        MoveActionHandler handler = builder()
                .snapshot(snapshot)
                .movedNode(selected)
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
        RemoveActionHandler handler = new RemoveActionHandler(snapshot, actionRemove);
        handler.handle();
    }

    @Override
    public Optional<GraphNode> onAdd(Graphics2D graphics, Point dropPoint, Transferable transferable, ImageObserver observer) {

        return componentDescriptorOf(transferable).map(descriptor -> {

            GraphNode nodeToAdd = GraphNodeFactory.get(descriptor);

            ComponentData componentData = nodeToAdd.componentData();

            List<ComponentPropertyDescriptor> propertiesDescriptors = componentData.getPropertiesDescriptors();

            // Fill default property values for the just added component
            DefaultDescriptorDataValuesFiller.fill(componentData, propertiesDescriptors);

            LOG.info(format("Node Dropped [%s], drop point [x: %d, y: %d]", PrintFlowInfo.name(nodeToAdd), dropPoint.x, dropPoint.y));

            Action addAction = getActionAdd(nodeToAdd, dropPoint, graphics, observer);

            AddActionHandler handler = new AddActionHandler(snapshot, addAction);

            return handler.handle() ? nodeToAdd : null;

        });
    }

    protected abstract Action getActionRemove(GraphNode nodeToRemove);

    protected abstract Action getActionReplace(GraphNode nodeFrom, GraphNode nodeTo);

    protected abstract Action getActionAdd(GraphNode nodeToAdd, Point dropPoint, Graphics2D graphics, ImageObserver observer);

    private Optional<ComponentDescriptor> componentDescriptorOf(Transferable transferable) {
        DataFlavor[] transferDataFlavor = transferable.getTransferDataFlavors();
        if (asList(transferDataFlavor).contains(FLAVOR)) {
            try {
                return Optional.of((ComponentDescriptor) transferable.getTransferData(FLAVOR));
            } catch (UnsupportedFlavorException | IOException e) {
                LOG.error("Could not extract dropped component name", e);
            }
        }
        return Optional.empty();
    }
}