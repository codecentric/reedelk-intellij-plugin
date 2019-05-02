package com.esb.plugin.designer.canvas.drawables;

import com.esb.plugin.component.Component;
import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.properties.PropertyTracker;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.Drawable;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedNode;
import com.esb.plugin.graph.scope.FindScope;
import com.esb.plugin.graph.scope.ListLastNodeOfScope;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.List;
import java.util.Optional;

public abstract class AbstractGraphNode implements GraphNode {

    private final Component component;

    private final Drawable icon;
    private final Drawable arrows;
    private final Drawable componentTitle;
    private final Drawable selectedItemBox;
    private final Drawable componentDescription;

    // x and y represent the center position of this Drawable.
    private int x;
    private int y;

    // represent the coordinates of the dragged node
    private int draggedX;
    private int draggedY;

    private boolean dragging;
    private boolean selected;

    public AbstractGraphNode(Component component) {
        this.component = component;
        icon = new Icon(component.getFullyQualifiedName());

        componentTitle = new ComponentTitle(component.getDisplayName());

        PropertyTracker tracker = new PropertyTracker(component, ComponentDescription.DESCRIPTION_PROPERTY_NAME);
        componentDescription = new ComponentDescription(tracker);

        arrows = new Arrows(this);
        selectedItemBox = new SelectedItem(this);
    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        int iconDrawableHeight = icon.height(graphics);
        int halfIconDrawableHeight = Math.floorDiv(iconDrawableHeight, 2);

        int componentTitleHeight = componentTitle.height(graphics);
        int halfComponentTitleHeight = Math.floorDiv(componentTitleHeight, 2);

        int componentDescriptionHeight = componentDescription.height(graphics);
        int halfComponentDescriptionHeight = Math.floorDiv(componentDescriptionHeight, 2);

        int totalHeight = iconDrawableHeight + componentTitleHeight + componentDescriptionHeight;
        int halfTotalHeight = Math.floorDiv(totalHeight, 2);

        // Center icon
        int centerIconY = y() - halfTotalHeight + halfIconDrawableHeight;
        icon.setPosition(x(), centerIconY);

        // Center title below icon
        int centerTitleY = y() - halfTotalHeight + iconDrawableHeight + halfComponentTitleHeight;
        componentTitle.setPosition(x(), centerTitleY);

        // Center description below title
        int centerDescriptionY = y() - halfTotalHeight + iconDrawableHeight + componentTitleHeight + halfComponentDescriptionHeight;
        componentDescription.setPosition(x(), centerDescriptionY);

        // Center selected box
        selectedItemBox.setPosition(x(), y());

        if (dragging) {
            // Dragging positions we need to center again
            centerIconY = draggedY - halfTotalHeight + halfIconDrawableHeight;
            icon.drag(draggedX, centerIconY);

            centerTitleY = draggedY - halfTotalHeight + iconDrawableHeight + halfComponentTitleHeight;
            componentTitle.drag(draggedX, centerTitleY);

            centerDescriptionY = draggedY - halfTotalHeight + iconDrawableHeight + componentTitleHeight + halfComponentDescriptionHeight;
            componentDescription.drag(draggedX, centerDescriptionY);
        }


        drawConnections(graph, graphics, observer);

        icon.draw(graph, graphics, observer);
        componentTitle.draw(graph, graphics, observer);
        componentDescription.draw(graph, graphics, observer);
        selectedItemBox.draw(graph, graphics, observer);
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
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int height(Graphics2D graphics) {
        return Tile.HEIGHT;
    }

    @Override
    public int width(Graphics2D graphics) {
        return Tile.WIDTH;
    }

    @Override
    public boolean contains(ImageObserver observer, int x, int y) {
        return icon.contains(observer, x, y);
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void selected() {
        selected = true;
        icon.selected();
        selectedItemBox.selected();
        componentTitle.selected();
        componentDescription.selected();
    }

    @Override
    public void unselected() {
        selected = false;
        icon.unselected();
        selectedItemBox.unselected();
        componentTitle.unselected();
        componentDescription.unselected();
    }

    @Override
    public void drag(int x, int y) {
        draggedX = x;
        draggedY = y;

        icon.drag(x, y);
        selectedItemBox.drag(x, y);
        componentTitle.drag(x, y);
        componentDescription.drag(x, y);
    }

    @Override
    public void dragging() {
        dragging = true;

        icon.dragging();
        selectedItemBox.dragging();
        componentTitle.dragging();
        componentDescription.dragging();
    }

    @Override
    public void drop() {
        dragging = false;

        icon.drop();
        selectedItemBox.drop();
        componentTitle.drop();
        componentDescription.drop();
    }

    @Override
    public Component component() {
        return component;
    }

    @Override
    public Point getBarycenter(Graphics2D graphics) {
        // It it is the center of the Icon.
        int iconDrawableHeight = icon.height(graphics);
        int componentTitleHeight = componentTitle.height(graphics);
        int componentDescriptionHeight = componentDescription.height(graphics);

        int totalHeight = iconDrawableHeight + componentTitleHeight + componentDescriptionHeight;

        // Center icon
        int centerIconY = y() - Math.floorDiv(totalHeight, 2) + Math.floorDiv(iconDrawableHeight, 2);
        return new Point(x(), centerIconY);
    }

    /**
     * Draws connections between this node and the next one. If this is the last
     * node of the scope, don't draw any outgoing arrow.
     */
    protected void drawConnections(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        Optional<ScopedNode> wrappingScope = FindScope.of(graph, this);
        if (wrappingScope.isPresent()) {
            List<GraphNode> nodesBelongingWrappingScope = ListLastNodeOfScope.from(graph, wrappingScope.get());
            if (nodesBelongingWrappingScope.contains(this)) {
                // last node of the scope. Don't draw any outgoing arrow.
                return;
            }
        }
        arrows.draw(graph, graphics, observer);
    }

    @Override
    public String toString() {
        return component.getFullyQualifiedName();
    }
}
