package com.reedelk.plugin.editor.designer;

import com.reedelk.module.descriptor.model.ComponentType;
import com.reedelk.plugin.commons.Half;
import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.editor.designer.arrow.Arrow;
import com.reedelk.plugin.editor.designer.icon.Icon;
import com.reedelk.plugin.editor.designer.icon.IconDragging;
import com.reedelk.plugin.editor.designer.icon.IconRemoveComponent;
import com.reedelk.plugin.editor.designer.scopebox.SelectedBox;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.graph.node.ScopedGraphNode;
import com.reedelk.plugin.graph.utils.FindScope;
import com.reedelk.plugin.graph.utils.ListLastNodesOfScope;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;
import java.util.List;
import java.util.Optional;

public abstract class AbstractGraphNode implements GraphNode {

    public static final int NODE_WIDTH = 130;
    public static final int HALF_NODE_WIDTH = Half.of(NODE_WIDTH);

    protected final Icon icon;

    private final Arrow solidArrow;
    private final Icon draggedIcon;
    private final SelectedBox selectedBox;
    private final ComponentData componentData;
    private final IconRemoveComponent iconRemoveComponent;

    // x and y represent the center position of this Node on the canvas.
    private int x;
    private int y;

    // represent the coordinates of the dragged node
    private int draggedX;
    private int draggedY;

    private boolean dragging;
    private boolean selected;

    public AbstractGraphNode(ComponentData componentData) {
        this.componentData = componentData;
        this.solidArrow = new Arrow();
        this.icon = new Icon(componentData);
        this.selectedBox = new SelectedBox();
        this.draggedIcon = new IconDragging(componentData);
        this.iconRemoveComponent = new IconRemoveComponent();
    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        if (selected) {
            // Draw the background box of this selected component
            selectedBox.setPosition(x - Half.of(width(graphics)) + 2, y);
            selectedBox.draw(graphics, width(graphics), bottomHalfHeight(graphics));
            drawRemoveComponentIcon(graphics, observer);
        }
        icon.draw(graphics, observer);
    }

    @Override
    public void drawDrag(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        if (dragging) {
            draggedIcon.setPosition(draggedX, draggedY);
            draggedIcon.draw(graphics, observer);
        }
    }

    @Override
    public void mouseMoved(DrawableListener listener, MouseEvent event) {
        int mouseMovedX = event.getX();
        int mouseMovedY = event.getY();
        if (icon.contains(mouseMovedX, mouseMovedY)) {
            listener.setTheCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        // The hand cursor over the remove icon is visible
        // if and only if the icon is selected.
        if (selected && iconRemoveComponent.withinBounds(mouseMovedX, mouseMovedY)) {
            listener.setTheCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }

    @Override
    public void mousePressed(DrawableListener listener, MouseEvent event) {
        int mousePressedX = event.getX();
        int mousePressedY = event.getY();
        // If the mouse x,y coordinates are within the remove icon,
        // and the component is currently selected, then we remove the component.
        if (selected && iconRemoveComponent.withinBounds(mousePressedX, mousePressedY)) {
            listener.removeComponent(this);
        }
    }

    @Override
    public int x() {
        return x;
    }

    @Override
    public int y() {
        return y;
    }

    @Override
    public int width(Graphics2D graphics) {
        return NODE_WIDTH;
    }

    @Override
    public int height(Graphics2D graphics) {
        return icon.height(graphics);
    }

    @Override
    public int topHalfHeight(Graphics2D graphics) {
        return icon.topHalfHeight();
    }

    @Override
    public int bottomHalfHeight(Graphics2D graphics) {
        return icon.bottomHalfHeight(graphics);
    }

    @Override
    public boolean contains(ImageObserver observer, int x, int y) {
        return icon.contains(x, y);
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        this.icon.setPosition(x, y);
    }

    @Override
    public void drawArrows(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        internalDrawArrows(graph, graphics);
    }

    @Override
    public void selected() {
        selected = true;
        icon.selected();
    }

    @Override
    public void unselected() {
        selected = false;
        icon.unselected();
    }

    @Override
    public boolean isDraggable() {
        // INBOUND Components cannot be dragged.
        return ComponentType.INBOUND != getComponentClass();
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    public void drag(int x, int y) {
        draggedX = x;
        draggedY = y;
    }

    @Override
    public void dragging() {
        dragging = true;
    }

    @Override
    public void drop() {
        dragging = false;
    }

    @Override
    public ComponentData componentData() {
        return componentData;
    }

    @Override
    public Point getTargetArrowEnd() {
        return icon.getTargetArrowEnd();
    }

    @Override
    public Point getSourceArrowStart() {
        return icon.getSourceArrowStart();
    }

    @Override
    public ComponentType getComponentClass() {
        return componentData.getComponentClass();
    }

    @Override
    public String toString() {
        return componentData.getFullyQualifiedName();
    }

    protected void drawRemoveComponentIcon(Graphics2D graphics, ImageObserver observer) {
        int topRightX = x() + Half.of(icon.width());
        int topRightY = y() - icon.topHalfHeight() + Icon.Dimension.TOP_PADDING;
        iconRemoveComponent.setPosition(topRightX, topRightY);
        iconRemoveComponent.draw(graphics, observer);
    }

    /**
     * Draws connections between this node and the next one. If this is the last
     * node of the scope, don't draw any outgoing arrow.
     */
    private void internalDrawArrows(FlowGraph graph, Graphics2D graphics) {
        Optional<ScopedGraphNode> wrappingScope = FindScope.of(graph, this);
        if (wrappingScope.isPresent()) {
            List<GraphNode> nodesBelongingWrappingScope = ListLastNodesOfScope.from(graph, wrappingScope.get());
            if (nodesBelongingWrappingScope.contains(this)) {
                // last node of the scope.
                // Don't draw any outgoing arrow.
                return;
            }
        }
        drawTheArrows(graph, graphics);
    }

    private void drawTheArrows(FlowGraph graph, Graphics2D graphics) {
        graph.successors(this).forEach(successor -> {
            Point thisNodeArrowStart = getSourceArrowStart();
            Point successorNodeArrowEnd = successor.getTargetArrowEnd();
            solidArrow.draw(thisNodeArrowStart, successorNodeArrowEnd, graphics);
        });
    }
}
