package com.esb.plugin.editor.designer.drawables;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.Drawable;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;

public abstract class AbstractText implements Drawable {

    private int x;
    private int y;

    private int draggedX;
    private int draggedY;
    private boolean dragging;


    protected abstract String getText();

    protected abstract Color getColor();

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        graphics.setColor(getColor());

        int halfWidth = Math.floorDiv(width(graphics), 2);
        int halfHeight = Math.floorDiv(height(graphics), 2);
        int startX = x() - halfWidth;
        int startY = y() + halfHeight;
        graphics.drawString(getText(), startX, startY);

        if (dragging) {
            startX = draggedX - halfWidth;
            startY = draggedY + halfHeight;
            graphics.drawString(getText(), startX, startY);
        }
    }

    @Override
    public int y() {
        return y;
    }

    @Override
    public int x() {
        return x;
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int height(Graphics2D graphics) {
        Rectangle2D stringBounds = graphics.getFontMetrics().getStringBounds(getText(), graphics);
        return (int) stringBounds.getHeight();
    }

    @Override
    public int width(Graphics2D graphics) {
        Rectangle2D stringBounds = graphics.getFontMetrics().getStringBounds(getText(), graphics);
        return (int) stringBounds.getWidth();
    }

    @Override
    public void drop() {
        this.dragging = false;
    }

    @Override
    public void dragging() {
        this.dragging = true;
    }

    @Override
    public void drag(int x, int y) {
        this.draggedX = x;
        this.draggedY = y;
    }

}
