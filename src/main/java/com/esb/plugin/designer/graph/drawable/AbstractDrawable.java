package com.esb.plugin.designer.graph.drawable;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.editor.component.ComponentDescriptor;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.decorators.*;
import com.esb.plugin.designer.graph.scope.FindScope;
import com.esb.plugin.designer.graph.scope.ListLastNodeOfScope;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.Optional;

public abstract class AbstractDrawable implements Drawable {

    private final ComponentDescriptor component;

    private final Drawable iconDrawable;
    private final Drawable arrowsDrawable;
    private final Drawable selectedItemDrawable;
    private final Drawable componentTitleDrawable;
    private final Drawable componentDescriptionDrawable;

    // x and y represent the center position os this Drawable.
    private int x;
    private int y;

    // represent the coordinates of the dragged drawable
    private int draggedX;
    private int draggedY;

    private boolean dragging;
    private boolean selected;

    public AbstractDrawable(ComponentDescriptor component) {
        this.component = component;
        this.iconDrawable = new IconDrawable(component.getDisplayName());
        this.componentTitleDrawable = new ComponentTitleDrawable(displayName());
        this.componentDescriptionDrawable = new ComponentDescriptionDrawable("A description");

        this.arrowsDrawable = new ArrowsDrawable(this);
        this.selectedItemDrawable = new SelectedItemDrawable(this);
    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        int iconDrawableHeight = iconDrawable.height(graphics);
        int halfIconDrawableHeight = Math.floorDiv(iconDrawableHeight, 2);

        int componentTitleHeight = componentTitleDrawable.height(graphics);
        int halfComponentTitleHeight = Math.floorDiv(componentTitleHeight, 2);

        int componentDescriptionHeight = componentDescriptionDrawable.height(graphics);
        int halfComponentDescriptionHeight = Math.floorDiv(componentDescriptionHeight, 2);

        int totalHeight = iconDrawableHeight + componentTitleHeight + componentDescriptionHeight;
        int halfTotalHeight = Math.floorDiv(totalHeight, 2);

        // Center icon
        int centerIconY = y() - halfTotalHeight + halfIconDrawableHeight;
        iconDrawable.setPosition(x(), centerIconY);

        // Center title below icon
        int centerTitleY = y() - halfTotalHeight + iconDrawableHeight + halfComponentTitleHeight;
        componentTitleDrawable.setPosition(x(), centerTitleY);

        // Center description below title
        int centerDescriptionY = y() - halfTotalHeight + iconDrawableHeight + componentTitleHeight + halfComponentDescriptionHeight;
        componentDescriptionDrawable.setPosition(x(), centerDescriptionY);

        // Center selected box
        selectedItemDrawable.setPosition(x(), y());

        if (dragging) {
            // Dragging positions we need to center again
            centerIconY = draggedY - halfTotalHeight + halfIconDrawableHeight;
            iconDrawable.drag(draggedX, centerIconY);

            centerTitleY = draggedY - halfTotalHeight + iconDrawableHeight + halfComponentTitleHeight;
            componentTitleDrawable.drag(draggedX, centerTitleY);

            centerDescriptionY = draggedY - halfTotalHeight + iconDrawableHeight + componentTitleHeight + halfComponentDescriptionHeight;
            componentDescriptionDrawable.drag(draggedX, centerDescriptionY);
        }


        drawConnections(graph, graphics, observer);

        iconDrawable.draw(graph, graphics, observer);
        componentTitleDrawable.draw(graph, graphics, observer);
        componentDescriptionDrawable.draw(graph, graphics, observer);
        selectedItemDrawable.draw(graph, graphics, observer);
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
        return iconDrawable.contains(observer, x, y);
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void selected() {
        selected = true;
        iconDrawable.selected();
        selectedItemDrawable.selected();
        componentTitleDrawable.selected();
        componentDescriptionDrawable.selected();
    }

    @Override
    public void unselected() {
        selected = false;
        iconDrawable.unselected();
        selectedItemDrawable.unselected();
        componentTitleDrawable.unselected();
        componentDescriptionDrawable.unselected();
    }

    @Override
    public void drag(int x, int y) {
        draggedX = x;
        draggedY = y;

        iconDrawable.drag(x, y);
        selectedItemDrawable.drag(x, y);
        componentTitleDrawable.drag(x, y);
        componentDescriptionDrawable.drag(x, y);
    }

    @Override
    public void dragging() {
        dragging = true;

        iconDrawable.dragging();
        selectedItemDrawable.dragging();
        componentTitleDrawable.dragging();
        componentDescriptionDrawable.dragging();
    }

    @Override
    public void drop() {
        dragging = false;

        iconDrawable.drop();
        selectedItemDrawable.drop();
        componentTitleDrawable.drop();
        componentDescriptionDrawable.drop();
    }

    @Override
    public ComponentDescriptor component() {
        return component;
    }

    @Override
    public String displayName() {
        return component.getDisplayName();
    }

    @Override
    public Point getBarycenter(Graphics2D graphics) {
        // It it is the center of the Icon.
        int iconDrawableHeight = iconDrawable.height(graphics);
        int componentTitleHeight = componentTitleDrawable.height(graphics);
        int componentDescriptionHeight = componentDescriptionDrawable.height(graphics);

        int totalHeight = iconDrawableHeight + componentTitleHeight + componentDescriptionHeight;

        // Center icon
        int centerIconY = y() - Math.floorDiv(totalHeight, 2) + Math.floorDiv(iconDrawableHeight, 2);
        return new Point(x(), centerIconY);
    }

    // TODO: refine this
    protected void drawConnections(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        // If this is the last of the scope, don't draw anything.
        Optional<ScopedDrawable> wrappingScope = FindScope.of(graph, this);
        if (wrappingScope.isPresent()) {
            if (ListLastNodeOfScope.from(graph, wrappingScope.get()).contains(this)) {
                // do nothing
                return;
            }
        }
        arrowsDrawable.draw(graph, graphics, observer);
    }
}
