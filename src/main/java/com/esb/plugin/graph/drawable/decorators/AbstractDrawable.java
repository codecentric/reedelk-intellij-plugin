package com.esb.plugin.graph.drawable.decorators;

import com.esb.plugin.graph.drawable.Drawable;

import java.awt.*;
import java.awt.image.ImageObserver;

public abstract class AbstractDrawable implements Drawable {

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
    public void selected() {

    }

    @Override
    public void unselected() {
    }

    @Override
    public int y() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int x() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPosition(int x, int y) {
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
    public boolean contains(ImageObserver observer, int x, int y) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public Point getBarycenter(Graphics2D graphics) {
        throw new UnsupportedOperationException();
    }

}
