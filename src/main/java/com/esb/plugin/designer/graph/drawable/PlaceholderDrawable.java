package com.esb.plugin.designer.graph.drawable;

import com.esb.plugin.designer.graph.FlowGraph;
import com.intellij.ui.JBColor;

import java.awt.*;
import java.awt.image.ImageObserver;

import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_ROUND;

public class PlaceholderDrawable implements Drawable {

    private static final int INNER_PADDING = 3;
    private final Stroke DOTTED_STROKE = new BasicStroke(0.7f, CAP_ROUND, JOIN_ROUND, 0, new float[]{3}, 0);

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        graphics.setStroke(DOTTED_STROKE);
        graphics.setColor(JBColor.lightGray);

        int halfWidth = Math.floorDiv(width(graphics), 2);
        int halfHeight = Math.floorDiv(height(graphics), 2);
        int x1 = x() - halfWidth + INNER_PADDING;
        int y1 = y() - halfHeight + INNER_PADDING;
        int x2 = x() + halfWidth - INNER_PADDING;
        int y2 = y() - halfHeight + INNER_PADDING;
        int x3 = x() - halfWidth + INNER_PADDING;
        int y3 = y() + halfHeight - INNER_PADDING;
        int x4 = x() + halfWidth - INNER_PADDING;
        int y4 = y() + halfHeight - INNER_PADDING;

        graphics.drawLine(x1, y1, x2, y2);
        graphics.drawLine(x2, y2, x4, y4);
        graphics.drawLine(x4, y4, x3, y3);
        graphics.drawLine(x3, y3, x1, y1);
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

    @Override
    public void drag(int x, int y) {

    }

    @Override
    public void dragging() {

    }

    @Override
    public void release() {

    }
}
