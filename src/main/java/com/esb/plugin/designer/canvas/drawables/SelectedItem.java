package com.esb.plugin.designer.canvas.drawables;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.Drawable;
import com.intellij.ui.JBColor;

import java.awt.*;
import java.awt.image.ImageObserver;

import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_ROUND;

public class SelectedItem implements Drawable {

    private static final int INNER_PADDING = 3;
    private final Stroke DOTTED_STROKE = new BasicStroke(0.7f, CAP_ROUND, JOIN_ROUND, 0, new float[]{3}, 0);

    private final Drawable parent;

    private int x;
    private int y;
    private boolean selected;

    public SelectedItem(Drawable parent) {
        this.parent = parent;
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
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        if (!selected) return;

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
    public int height(Graphics2D graphics) {
        return parent.height(graphics);
    }

    @Override
    public int width(Graphics2D graphics) {
        return parent.width(graphics);
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void selected() {
        selected = true;
    }

    @Override
    public void unselected() {
        selected = false;
    }

}
