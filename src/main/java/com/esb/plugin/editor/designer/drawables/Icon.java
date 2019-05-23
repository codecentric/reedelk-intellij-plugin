package com.esb.plugin.editor.designer.drawables;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.Drawable;

import java.awt.*;
import java.awt.image.ImageObserver;

public class Icon implements Drawable {

    private static final int WIDTH = 60;
    private static final int HEIGHT = 60;

    private final Image image;

    private int x;
    private int y;

    private int draggedX;
    private int draggedY;
    private boolean dragging;

    public Icon(Image image) {
        this.image = image;
    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        int halfWidth = Math.floorDiv(image.getWidth(observer), 2);
        int halfHeight = Math.floorDiv(image.getHeight(observer), 2);
        int imageX = x() - halfWidth;
        int imageY = y() - halfHeight;
        graphics.drawImage(image, imageX, imageY, observer);

        if (dragging) {
            imageX = draggedX - halfWidth;
            imageY = draggedY - halfHeight;
            graphics.drawImage(image, imageX, imageY, observer);
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
    public int height(Graphics2D graphics) {
        return HEIGHT;
    }

    @Override
    public int width(Graphics2D graphics) {
        return WIDTH;
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean contains(ImageObserver observer, int x, int y) {
        int halfImageWidth = Math.floorDiv(image.getWidth(observer), 2);
        int halfImageHeight = Math.floorDiv(image.getHeight(observer), 2);

        boolean containsOnXAxis =
                x >= this.x - halfImageWidth &&
                        x <= this.x + halfImageWidth;

        boolean containsOnYAxis =
                y >= this.y - halfImageHeight &&
                        y <= this.y + halfImageHeight;
        return containsOnXAxis && containsOnYAxis;
    }

    @Override
    public void drag(int x, int y) {
        this.draggedX = x;
        this.draggedY = y;
    }

    @Override
    public void dragging() {
        this.dragging = true;
    }

    @Override
    public void drop() {
        this.dragging = false;
    }

}
