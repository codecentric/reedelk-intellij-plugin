package com.esb.plugin.designer.graph.drawable.decorators;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;

import java.awt.*;
import java.awt.image.ImageObserver;

public class NothingSelectedDrawable implements Drawable {
    @Override
    public void drag(int x, int y) {

    }

    @Override
    public void dragging() {

    }

    @Override
    public void release() {

    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {

    }

    @Override
    public int y() {
        return 0;
    }

    @Override
    public int x() {
        return 0;
    }

    @Override
    public void setPosition(int x, int y) {

    }

    @Override
    public int height(Graphics2D graphics) {
        return 0;
    }

    @Override
    public int width(Graphics2D graphics) {
        return 0;
    }

    @Override
    public boolean contains(ImageObserver observer, int x, int y) {
        return false;
    }

    @Override
    public void selected() {

    }

    @Override
    public void unselected() {

    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public Point getBaryCenter(Graphics2D graphics) {
        return null;
    }
}
