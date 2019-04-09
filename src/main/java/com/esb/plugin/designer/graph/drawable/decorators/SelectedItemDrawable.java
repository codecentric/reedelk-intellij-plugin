package com.esb.plugin.designer.graph.drawable.decorators;

import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.intellij.ui.JBColor;

import java.awt.*;
import java.awt.image.ImageObserver;

import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_ROUND;

public class SelectedItemDrawable implements Drawable {

    private static final int INNER_PADDING = 4;
    private final Stroke DOTTED_STROKE = new BasicStroke(0.7f, CAP_ROUND, JOIN_ROUND, 0, new float[]{3}, 0);

    private final Drawable parent;


    private boolean selected;
    private int x;
    private int y;

    public SelectedItemDrawable(Drawable parent) {
        this.parent = parent;
    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        if (selected) {
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
        return false;
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
    public void selected() {
        this.selected = true;
    }

    @Override
    public void unselected() {
        this.selected = false;
    }

    @Override
    public Point getBaryCenter(Graphics2D graphics) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Component component() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String displayName() {
        throw new UnsupportedOperationException();
    }
}
