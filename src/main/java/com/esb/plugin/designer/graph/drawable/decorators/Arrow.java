package com.esb.plugin.designer.graph.drawable.decorators;

import com.intellij.ui.JBColor;

import java.awt.*;
import java.awt.geom.GeneralPath;

public class Arrow {

    private static final JBColor ARROW_COLOR = JBColor.lightGray;
    private static final int ARROW_SIZE = 10;
    private static final double ARROW_ANGLE = Math.PI / 5.0d;
    private final Stroke STROKE = new BasicStroke(1f);
    private final Point source;
    private final Point target;

    public Arrow(final Point source, final Point target) {
        this.source = source;
        this.target = target;
    }

    public void draw(Graphics2D graphics) {
        graphics.setStroke(STROKE);
        graphics.setColor(ARROW_COLOR);

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
