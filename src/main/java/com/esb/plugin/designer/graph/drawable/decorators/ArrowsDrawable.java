package com.esb.plugin.designer.graph.drawable.decorators;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;

import java.awt.*;
import java.awt.image.ImageObserver;

public class ArrowsDrawable implements Drawable {

    private final Drawable parent;
    private int x;
    private int y;

    public ArrowsDrawable(Drawable parent) {
        this.parent = parent;
    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        // TODO: This is not good here
        if (parent instanceof ScopedDrawable) return;

        graph.successors(parent)
                .forEach(successor -> {
                    Point sourceBaryCenter = parent.getBarycenter(graphics);
                    Point source = new Point(
                            sourceBaryCenter.x + Math.floorDiv(Tile.WIDTH, 2) - 15,
                            sourceBaryCenter.y);

                    Point target = new Point(
                            successor.getBarycenter(graphics).x - Math.floorDiv(Tile.WIDTH, 2) + 15,
                            successor.getBarycenter(graphics).y);
                    Arrow arrow = new Arrow(source, target);
                    arrow.draw(graphics);
                });
    }

    @Override
    public void drag(int x, int y) {
    }

    @Override
    public void dragging() {
    }

    @Override
    public void drop() {
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
    public boolean contains(ImageObserver observer, int x, int y) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int height(Graphics2D graphics) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int width(Graphics2D graphics) {
        throw new UnsupportedOperationException();
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
    public Point getBarycenter(Graphics2D graphics) {
        throw new UnsupportedOperationException();
    }

}


