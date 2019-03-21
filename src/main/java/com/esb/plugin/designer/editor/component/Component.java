package com.esb.plugin.designer.editor.component;

import com.esb.plugin.designer.editor.Drawable;
import com.esb.plugin.designer.editor.Tile;
import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Component implements Drawable {

    private static final int COMPONENT_LABEL_PADDING_TOP = 2;
    private final JPanel parent;
    private final Image componentImage;
    private final String componentName;
    private Point topLeft;

    public Component(String componentName, JPanel parent, Point topLeft) {
        this.componentName = componentName;
        this.topLeft = topLeft;
        this.parent = parent;
        this.componentImage = Toolkit.getDefaultToolkit().getImage("/Users/lorenzo/IdeaProjects/drag-and-drop/resources/fork.png");
    }

    @Override
    public void draw(Graphics g) {
        // If goes beyond the width (or height) update the set preferred Dimension of the parent.
        int offset = Math.floorDiv(Tile.INSTANCE.width - width(), 2);
        g.drawImage(componentImage, topLeft.x + offset, topLeft.y + offset, parent);
        g.setColor(JBColor.GRAY);

        // Compute center of the tile
        drawComponentNameLabel(g, componentName, Tile.INSTANCE.width, topLeft.x, topLeft.y + height() + offset);

    }

    private void drawComponentNameLabel(Graphics g, String s, int width, int XPos, int YPos) {
        Rectangle2D stringBounds = g.getFontMetrics().getStringBounds(s, g);
        int stringWidth = (int) stringBounds.getWidth();
        int stringHeight = (int) stringBounds.getHeight();
        int startX = width / 2 - stringWidth / 2;
        int startY = YPos + stringHeight + COMPONENT_LABEL_PADDING_TOP;
        g.drawString(s, startX + XPos, startY);
    }

    @Override
    public boolean contains(Point point) {
        boolean withinXBounds = point.x >= topLeft.x && point.x <= topLeft.x + Tile.INSTANCE.width;
        boolean withinYBounds = point.y >= topLeft.y && point.y <= topLeft.y + Tile.INSTANCE.height;
        return withinXBounds && withinYBounds;
    }

    @Override
    public Point getPosition() {
        return topLeft;
    }

    @Override
    public void setPosition(Point point) {
        this.topLeft = point;
    }

    @Override
    public int width() {
        return componentImage.getWidth(parent);
    }

    @Override
    public int height() {
        return componentImage.getHeight(parent);
    }

}
