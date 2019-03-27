package com.esb.plugin.designer.graph.builder;

import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.editor.component.Component;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;

public class DrawableChoice extends AbstractDrawable {

    private final Image image = Toolkit.getDefaultToolkit().getImage("/Users/lorenzo/Desktop/fork.png");

    public DrawableChoice(Component component) {
        super(component);
    }

    @Override
    public void draw(Graphics graphics, ImageObserver observer) {
        Rectangle2D stringBounds = graphics.getFontMetrics().getStringBounds(stringValue(), graphics);
        int width = (int) Math.floor(stringBounds.getWidth());
        int height = (int) Math.floor(stringBounds.getHeight());

        int circleWidth = Math.floorDiv(Tile.WIDTH, 2);
        int circleHeight = Math.floorDiv(Tile.HEIGHT, 2);

        graphics.drawString(stringValue(), x() - Math.floorDiv(width, 2), y() + Math.floorDiv(height, 2));
        graphics.drawOval(x() - Math.floorDiv(circleWidth, 2), y() - Math.floorDiv(circleHeight, 2), circleWidth, circleHeight);
    }

    @Override
    public int width(ImageObserver imageObserver) {
        return 0;
    }

    @Override
    public int height(ImageObserver imageObserver) {
        return 0;
    }
}
