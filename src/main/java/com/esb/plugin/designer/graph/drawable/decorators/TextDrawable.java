package com.esb.plugin.designer.graph.drawable.decorators;

import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.intellij.ui.JBColor;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;

public abstract class TextDrawable implements Drawable {

    private final JBColor color;

    private int x;
    private int y;
    private String text;

    private int draggedX;
    private int draggedY;
    private boolean dragging;

    public TextDrawable(String text, JBColor color) {
        this.text = text;
        this.color = color;
    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        graphics.setColor(color);

        int halfWidth = Math.floorDiv(width(graphics), 2);
        int halfHeight = Math.floorDiv(height(graphics), 2);
        int startX = x() - halfWidth;
        int startY = y() + halfHeight;
        graphics.drawString(text, startX, startY);

        if (dragging) {
            startX = draggedX - halfWidth;
            startY = draggedY + halfHeight;
            graphics.drawString(text, startX, startY);
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
        Rectangle2D stringBounds = graphics.getFontMetrics().getStringBounds(text, graphics);
        return (int) stringBounds.getHeight();
    }

    @Override
    public int width(Graphics2D graphics) {
        Rectangle2D stringBounds = graphics.getFontMetrics().getStringBounds(text, graphics);
        return (int) stringBounds.getWidth();
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public void selected() {
    }

    @Override
    public void unselected() {
    }

    @Override
    public void release() {
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

    @Override
    public boolean contains(ImageObserver observer, int x, int y) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String displayName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Component component() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Point getBaryCenter(Graphics2D graphics) {
        throw new UnsupportedOperationException();
    }
}
