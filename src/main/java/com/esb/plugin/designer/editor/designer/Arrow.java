package com.esb.plugin.designer.editor.designer;

import com.intellij.ui.JBColor;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

public class Arrow {

    private static final int ARROW_SIZE = 10;
    private static final double ARROW_ANGLE = Math.PI / 5.0d;

    public static void draw(final Graphics2D gfx, final Point2D start, final Point2D end) {
        gfx.setColor(JBColor.lightGray);

        final double startx = start.getX();
        final double starty = start.getY();

        final double deltax = startx - end.getX();

        final double angle;
        if (deltax == 0.0d) {
            angle = Math.PI / 2;
        } else {
            angle = Math.atan((starty - end.getY()) / deltax) + (startx < end.getX() ? Math.PI : 0);
        }

        // Draws the small triangle at the end of the arrow.
        final double x1 = ARROW_SIZE * Math.cos(angle - ARROW_ANGLE);
        final double y1 = ARROW_SIZE * Math.sin(angle - ARROW_ANGLE);
        final double x2 = ARROW_SIZE * Math.cos(angle + ARROW_ANGLE);
        final double y2 = ARROW_SIZE * Math.sin(angle + ARROW_ANGLE);

        final double cx = (ARROW_SIZE / 2.0f) * Math.cos(angle);
        final double cy = (ARROW_SIZE / 2.0f) * Math.sin(angle);

        final GeneralPath polygon = new GeneralPath();
        polygon.moveTo(end.getX(), end.getY());
        polygon.lineTo(end.getX() + x1, end.getY() + y1);
        polygon.lineTo(end.getX() + x2, end.getY() + y2);
        polygon.closePath();
        gfx.fill(polygon);

        // Draws the arrow's line from the start to the center of the small triangle
        gfx.drawLine((int) startx, (int) starty, (int) (end.getX() + cx), (int) (end.getY() + cy));

    }
}


