package com.esb.plugin.editor.designer;

import com.esb.plugin.commons.Half;
import com.esb.plugin.component.domain.ComponentClass;
import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.editor.designer.widget.Arrow;
import com.esb.plugin.editor.designer.widget.Icon;
import com.esb.plugin.editor.designer.widget.RemoveComponentIcon;
import com.esb.plugin.editor.designer.widget.SelectedBox;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.utils.FindScope;
import com.esb.plugin.graph.utils.ListLastNodesOfScope;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;
import java.util.List;
import java.util.Optional;

public abstract class AbstractGraphNode implements GraphNode {

    public static final int WIDTH = 130;

    private final ComponentData componentData;

    protected final Icon icon;
    private final Icon draggedIcon;
    private final SelectedBox selectedBox;
    private final RemoveComponentIcon removeComponentIcon;

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
        this.icon = new Icon(componentData);
        this.draggedIcon = new Icon(componentData);
        this.selectedBox = new SelectedBox();
        this.removeComponentIcon = new RemoveComponentIcon();
    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        if (selected) {
            // Draw the background box of a selected component
            selectedBox.setPosition(x, y);
            selectedBox.draw(this, graphics);
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
        int x = event.getX();
        int y = event.getY();
        if (icon.contains(x, y)) {
            listener.setTheCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        // The hand cursor over the remove icon is visible
        // if and only if the icon is selected.
        if (selected && removeComponentIcon.withinBounds(x, y)) {
            listener.setTheCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }

    @Override
    public void mousePressed(DrawableListener listener, MouseEvent event) {
        int x = event.getX();
        int y = event.getY();
        // If the mouse x,y coordinates are within the remove icon,
        // and the component is currently selected, then we remove the component.
        if (selected && removeComponentIcon.withinBounds(x, y)) {
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
        return WIDTH;
    }

    @Override
    public int height(Graphics2D graphics) {
        return icon.height(graphics);
    }

    @Override
    public int topHalfHeight(Graphics2D graphics) {
        return icon.topHalfHeight(graphics);
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
        _drawArrows(graph, graphics);
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
    public ComponentClass getComponentClass() {
        return componentData.getComponentClass();
    }

    @Override
    public String toString() {
        return componentData.getFullyQualifiedName();
    }


    protected void drawRemoveComponentIcon(Graphics2D graphics, ImageObserver observer) {
        int topRightX = x() + Half.of(icon.width());
        int topRightY = y() - icon.topHalfHeight(graphics) + Half.of(removeComponentIcon.height());
        removeComponentIcon.setPosition(topRightX, topRightY);
        removeComponentIcon.draw(graphics, observer);
    }

    /**
     * Draws connections between this node and the next one. If this is the last
     * node of the scope, don't draw any outgoing arrow.
     */
    private void _drawArrows(FlowGraph graph, Graphics2D graphics) {
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
            Arrow arrow = new Arrow(thisNodeArrowStart, successorNodeArrowEnd);
            arrow.draw(graphics);
        });
    }
}
