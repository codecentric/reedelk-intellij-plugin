package com.reedelk.plugin.editor.designer;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.reedelk.plugin.commons.DefaultDescriptorDataValuesFiller;
import com.reedelk.plugin.commons.PrintFlowInfo;
import com.reedelk.plugin.component.domain.ComponentClass;
import com.reedelk.plugin.component.domain.ComponentData;
import com.reedelk.plugin.component.domain.ComponentDescriptor;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.editor.designer.action.AddActionHandler;
import com.reedelk.plugin.editor.designer.action.MoveActionHandler;
import com.reedelk.plugin.editor.designer.action.RemoveActionHandler;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.action.Action;
import com.reedelk.plugin.graph.action.remove.strategy.PlaceholderProvider;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.GraphNodeFactory;
import com.reedelk.runtime.component.Placeholder;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.reedelk.plugin.editor.designer.action.MoveActionHandler.builder;
import static com.reedelk.plugin.editor.palette.ComponentDescriptorTransferable.FLAVOR;
import static java.awt.dnd.DnDConstants.ACTION_COPY_OR_MOVE;
import static java.lang.String.format;
import static java.util.Arrays.asList;

public abstract class AbstractDesignerPanelActionHandler implements DesignerPanelActionHandler {

    private static final Logger LOG = Logger.getInstance(AbstractDesignerPanelActionHandler.class);

    protected final PlaceholderProvider placeholderProvider;
    protected final FlowSnapshot snapshot;
    protected final Module module;

    protected AbstractDesignerPanelActionHandler(@NotNull Module module,
                                                 @NotNull FlowSnapshot snapshot) {
        this.placeholderProvider = () -> Optional.of(GraphNodeFactory.get(module, Placeholder.class.getName()));
        this.snapshot = snapshot;
        this.module = module;

    }

    @Override
    public void onMove(Graphics2D graphics, GraphNode selected, Point dropPoint, ImageObserver observer) {
        DimensionAwareDecorator placeHolderNode =
                new DimensionAwareDecorator(placeholderProvider.get().get(), selected);

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
                .placeholderProvider(placeholderProvider)
                .build();

        handler.handle();
    }

    @Override
    public void onRemove(GraphNode nodeToRemove) {
        Action actionRemove = getActionRemove(nodeToRemove);
        RemoveActionHandler handler =
                new RemoveActionHandler(placeholderProvider, snapshot, actionRemove);
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

            AddActionHandler handler = new AddActionHandler(placeholderProvider, snapshot, addAction);

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

    /**
     * This decorator, takes the delegate node and it applies the dimensions and positions
     * of the target node. This is needed for instance to make the Placeholder node
     * used in the move action of the same dimension of the given target dimension node.
     * Keeping the dimension and position of the placeholder equal to the target node allows
     * us to compute correctly the position of the moved node. The Placeholder must temporarily
     * occupy the same space size of the replaced object.
     */
    class DimensionAwareDecorator implements GraphNode {

        private final GraphNode targetDimensionNode;
        private final GraphNode delegate;

        DimensionAwareDecorator(GraphNode delegate, GraphNode targetDimensionNode) {
            this.delegate = delegate;
            this.targetDimensionNode = targetDimensionNode;
        }

        @Override
        public boolean isDraggable() {
            return delegate.isDraggable();
        }

        @Override
        public void drag(int x, int y) {
            delegate.drag(x, y);
        }

        @Override
        public void dragging() {
            delegate.dragging();
        }

        @Override
        public void drop() {
            delegate.drop();
        }

        @Override
        public void selected() {
            delegate.selected();
        }

        @Override
        public void unselected() {
            delegate.unselected();
        }

        @Override
        public boolean isSelected() {
            return delegate.isSelected();
        }

        @Override
        public void drawArrows(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
            delegate.drawArrows(graph, graphics, observer);
        }

        @Override
        public void drawDrag(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
            delegate.drawDrag(graph, graphics, observer);
        }

        @Override
        public int x() {
            return targetDimensionNode.x();
        }

        @Override
        public int y() {
            return targetDimensionNode.y();
        }

        @Override
        public int width(Graphics2D graphics) {
            return targetDimensionNode.width(graphics);
        }

        @Override
        public int height(Graphics2D graphics) {
            return targetDimensionNode.height(graphics);
        }

        @Override
        public int topHalfHeight(Graphics2D graphics) {
            return targetDimensionNode.topHalfHeight(graphics);
        }

        @Override
        public int bottomHalfHeight(Graphics2D graphics) {
            return targetDimensionNode.bottomHalfHeight(graphics);
        }

        @Override
        public void setPosition(int x, int y) {
            delegate.setPosition(x, y);
        }

        @Override
        public ComponentClass getComponentClass() {
            return delegate.getComponentClass();
        }

        @Override
        public boolean contains(ImageObserver observer, int x, int y) {
            return delegate.contains(observer, x, y);
        }

        @Override
        public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
            delegate.draw(graph, graphics, observer);
        }

        @Override
        public void mouseMoved(DrawableListener listener, MouseEvent event) {
            delegate.mouseMoved(listener, event);
        }

        @Override
        public void mousePressed(DrawableListener listener, MouseEvent event) {
            delegate.mousePressed(listener, event);
        }

        @Override
        public Point getTargetArrowEnd() {
            return delegate.getTargetArrowEnd();
        }

        @Override
        public Point getSourceArrowStart() {
            return delegate.getSourceArrowStart();
        }

        @Override
        public ComponentData componentData() {
            return delegate.componentData();
        }
    }
}