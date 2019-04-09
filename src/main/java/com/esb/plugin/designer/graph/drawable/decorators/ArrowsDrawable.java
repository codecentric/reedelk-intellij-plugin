package com.esb.plugin.designer.graph.drawable.decorators;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.intellij.ui.JBColor;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.ImageObserver;

public class ArrowsDrawable implements Drawable {

    private final Stroke STROKE = new BasicStroke(1f);
    private static final JBColor ARROW_COLOR = JBColor.lightGray;

    private static final int ARROW_SIZE = 10;
    private static final double ARROW_ANGLE = Math.PI / 5.0d;

    private final Drawable parent;
    private int x;
    private int y;

    public ArrowsDrawable(Drawable parent) {
        this.parent = parent;
    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        graphics.setStroke(STROKE);
        graphics.setColor(ARROW_COLOR);
        graph.successors(parent).forEach(successor -> {
            drawArrow(graphics, successor);
        });
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
    public Point getBaryCenter(Graphics2D graphics) {
        throw new UnsupportedOperationException();
    }


    private void drawArrow(Graphics2D graphics, Drawable targetDrawable) {
        Point sourceBaryCenter = parent.getBaryCenter(graphics);
        Point2D.Double source = new Point2D.Double(sourceBaryCenter.x + Math.floorDiv(Tile.WIDTH, 2) - 15, sourceBaryCenter.y);
        Point2D.Double target = new Point2D.Double(
                targetDrawable.getBaryCenter(graphics).x - Math.floorDiv(Tile.WIDTH, 2) + 15,
                targetDrawable.getBaryCenter(graphics).y);

        final double startx = source.getX();
        final double starty = source.getY();
        final double deltax = startx - target.getX();

        final double angle;
        if (deltax == 0.0d) {
            angle = Math.PI / 2;
        } else {
            angle = Math.atan((starty - target.getY()) / deltax) + (startx < target.getX() ? Math.PI : 0);
        }

        // Draws the small triangle at the end of the arrow.
        final double x1 = ARROW_SIZE * Math.cos(angle - ARROW_ANGLE);
        final double y1 = ARROW_SIZE * Math.sin(angle - ARROW_ANGLE);
        final double x2 = ARROW_SIZE * Math.cos(angle + ARROW_ANGLE);
        final double y2 = ARROW_SIZE * Math.sin(angle + ARROW_ANGLE);

        final double cx = (ARROW_SIZE / 2.0f) * Math.cos(angle);
        final double cy = (ARROW_SIZE / 2.0f) * Math.sin(angle);

        final GeneralPath polygon = new GeneralPath();
        polygon.moveTo(target.getX(), target.getY());
        polygon.lineTo(target.getX() + x1, target.getY() + y1);
        polygon.lineTo(target.getX() + x2, target.getY() + y2);
        polygon.closePath();
        graphics.fill(polygon);

        // Draws the arrow's line from the start to the center of the small triangle
        graphics.drawLine((int) startx, (int) starty, (int) (target.getX() + cx), (int) (target.getY() + cy));
    }

}


