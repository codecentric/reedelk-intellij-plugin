package com.reedelk.plugin.editor.designer.action;

import com.reedelk.module.descriptor.model.component.ComponentType;
import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.editor.designer.DrawableListener;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;

/**
 * This decorator, takes the delegate node and it applies the dimensions and positions
 * of the target node. This is needed for instance to make the Placeholder node
 * used in the move action of the same dimension of the given target dimension node.
 * Keeping the dimension and position of the placeholder equal to the target node allows
 * us to compute correctly the position of the moved node. The Placeholder must temporarily
 * occupy the same space size of the replaced object.
 */
class DimensionAwareGraphNodeDecorator implements GraphNode {

    private final GraphNode targetDimensionNode;
    private final GraphNode delegate;

    DimensionAwareGraphNodeDecorator(@NotNull GraphNode delegate, @NotNull GraphNode targetDimensionNode) {
        this.delegate = delegate;
        this.targetDimensionNode = targetDimensionNode;
    }

    @Override
    public boolean isDraggable() {
        return delegate.isDraggable();
    }

    @Override
    public boolean isSelectable() {
        return delegate.isSelectable();
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
    public ComponentType getComponentClass() {
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
