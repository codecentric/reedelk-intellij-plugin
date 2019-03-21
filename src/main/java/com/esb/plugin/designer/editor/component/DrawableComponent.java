package com.esb.plugin.designer.editor.component;

import com.esb.plugin.designer.editor.common.Tile;
import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class DrawableComponent implements Drawable {

    private static final int COMPONENT_LABEL_PADDING_TOP = 2;
    private final JPanel parent;
    private final Image componentImage;
    private final Component component;
    private Point topLeft;

    public DrawableComponent(Component component, JPanel parent, Point topLeft) {
        this.component = component;
        this.topLeft = topLeft;
        this.parent = parent;
        this.componentImage = Toolkit.getDefaultToolkit().getImage("/Users/lorenzo/IdeaProjects/drag-and-drop/resources/fork.png");
    }

    @Override
    public void draw(Graphics graphics) {
        // If goes beyond the width (or height) update the set preferred Dimension of the parent.

        int totalHeight = getIconHeight() +
                getComponentNameTextHeight(graphics) +
                getComponentDescriptionTextHeight(graphics);

        int verticalOffset = Math.floorDiv(Tile.INSTANCE.height - totalHeight, 2); // you can cache this value
        int horizontalOffset = Math.floorDiv(Tile.INSTANCE.width - width(), 2);

        graphics.drawImage(componentImage, topLeft.x + horizontalOffset, topLeft.y + verticalOffset, parent);
        graphics.setColor(JBColor.GRAY);

        // Compute center of the tile
        drawComponentNameLabel(graphics, component.getComponentName(), Tile.INSTANCE.width, topLeft.x, topLeft.y + height() + verticalOffset);
        drawComponentNameLabel(graphics, component.getComponentDescription(), Tile.INSTANCE.width, topLeft.x, topLeft.y + height() + verticalOffset + getComponentNameTextHeight(graphics));

    }

    private int getIconHeight() {
        return componentImage.getHeight(parent);
    }

    private int getIconWidth() {
        return componentImage.getWidth(parent);
    }

    private int getComponentNameTextHeight(Graphics graphics) {
        Rectangle2D stringBounds = graphics.getFontMetrics().getStringBounds("Sample", graphics);
        return (int) stringBounds.getHeight();
    }

    private int getComponentDescriptionTextHeight(Graphics graphics) {
        Rectangle2D stringBounds = graphics.getFontMetrics().getStringBounds("Sample", graphics);
        return (int) stringBounds.getHeight();
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
    public int width() {
        return componentImage.getWidth(parent);
    }

    @Override
    public int height() {
        return componentImage.getHeight(parent);
    }

}
