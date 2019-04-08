package com.esb.plugin.designer.graph.drawable;

import com.esb.plugin.commons.ESBIcons;
import com.esb.plugin.designer.Tile;
import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.FlowGraph;
import com.intellij.ui.JBColor;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;

abstract class AbstractDrawable implements Drawable {

    protected final Image image;
    private final Component component;

    private final int halfTileWidth;
    private final int halfTileHeight;

    // The center x position of this drawable
    private int x;

    // The center y position of this drawable
    private int y;

    public AbstractDrawable(Component component) {
        this.component = component;
        this.image = ESBIcons.forComponentAsImage(component.getName());

        this.halfTileWidth = Math.floorDiv(Tile.WIDTH, 2);
        this.halfTileHeight = Math.floorDiv(Tile.HEIGHT, 2);
    }

    @Override
    public void draw(FlowGraph graph, Graphics graphics, ImageObserver observer) {
        int imageX = x() - Math.floorDiv(image.getWidth(observer), 2);
        int imageY = y() - Math.floorDiv(image.getHeight(observer), 2);
        graphics.drawImage(image, imageX, imageY, observer);

        int textCenterX = x();
        int textTopY = y() + Math.floorDiv(image.getHeight(observer), 2);

        graphics.setColor(JBColor.GRAY);
        textTopY += drawText(graphics, displayName(), textCenterX, textTopY);

        graphics.setColor(JBColor.LIGHT_GRAY);
        drawText(graphics, "A Description", textCenterX, textTopY);

        // Draw bounding box:
        //drawBoundingBox(graphics);

    }

    @Override
    public Component component() {
        return component;
    }

    @Override
    public String displayName() {
        String[] segments = component.getName().split("\\.");
        return segments[segments.length - 1];
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int x() {
        return x;
    }

    @Override
    public int y() {
        return y;
    }

    @Override
    public int height() {
        return Tile.HEIGHT;
    }

    @Override
    public int width() {
        return Tile.WIDTH;
    }

    @Override
    public boolean contains(int x, int y) {

        boolean containsOnXAxis =
                x >= this.x - halfTileWidth &&
                        x <= this.x + halfTileWidth;

        boolean containsOnYAxis =
                y >= this.y - halfTileHeight &&
                        y <= this.y + halfTileHeight;

        return containsOnXAxis && containsOnYAxis;
    }

    private int drawText(Graphics graphics, String stringToDraw, int centerX, int topY) {
        Rectangle2D stringBounds = graphics.getFontMetrics().getStringBounds(stringToDraw, graphics);
        int stringWidth = (int) stringBounds.getWidth();
        int stringHeight = (int) stringBounds.getHeight();
        int startX = centerX - Math.floorDiv(stringWidth, 2);
        int startY = topY + stringHeight;
        graphics.drawString(stringToDraw, startX, startY);
        return stringHeight;
    }

    private void drawBoundingBox(Graphics graphics) {
        graphics.setColor(JBColor.RED);
        int x1 = x() - Math.floorDiv(width(), 2);
        int y1 = y() - Math.floorDiv(height(), 2);
        int x2 = x() + Math.floorDiv(width(), 2);
        int y2 = y() - Math.floorDiv(height(), 2);
        int x3 = x() - Math.floorDiv(width(), 2);
        int y3 = y() + Math.floorDiv(height(), 2);
        int x4 = x() + Math.floorDiv(width(), 2);
        int y4 = y() + Math.floorDiv(height(), 2);

        graphics.drawLine(x1, y1, x2, y2);
        graphics.drawLine(x2, y2, x4, y4);
        graphics.drawLine(x4, y4, x3, y3);
        graphics.drawLine(x3, y3, x1, y1);
    }
}
