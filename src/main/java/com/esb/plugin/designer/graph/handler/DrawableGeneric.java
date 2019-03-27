package com.esb.plugin.designer.graph.handler;

import com.esb.plugin.designer.editor.component.Component;

import java.awt.*;
import java.awt.image.ImageObserver;

public class DrawableGeneric extends AbstractDrawable {

    private final Image image = Toolkit.getDefaultToolkit().getImage("/Users/lorenzo/Desktop/log.png");

    public DrawableGeneric(Component component) {
        super(component);
    }

    @Override
    public void draw(Graphics graphics, ImageObserver observer) {

    }

    @Override
    public int width(ImageObserver imageObserver) {
        return 0;
    }

    @Override
    public int height(ImageObserver imageObserver) {
        return 0;
    }


    /**
     *     private static final int COMPONENT_LABEL_PADDING_TOP = 2;
     *     private final Image componentImage;
     *     private final Component component;
     *     private Point topLeft;
     *
     *     public DrawableComponent(Component component) {
     *         this.component = component;
     *         this.topLeft = new Point(0, 0);
     *         this.componentImage = Toolkit.getDefaultToolkit().getImage("/Users/lorenzo/IdeaProjects/drag-and-drop/resources/fork.png");
     *     }
     *
     *     @Override
     *     public void draw(Graphics graphics, ImageObserver observer) {
     *         // If goes beyond the width (or height) update the set preferred Dimension of the parent.
     *
     *         int totalHeight = getIconHeight(observer) +
     *                 getComponentNameTextHeight(graphics) +
     *                 getComponentDescriptionTextHeight(graphics);
     *
     *         int verticalOffset = Math.floorDiv(Tile.HEIGHT - totalHeight, 2); // you can cache this value
     *         int horizontalOffset = Math.floorDiv(Tile.INSTANCE.width - width(observer), 2);
     *
     *         graphics.drawImage(componentImage, topLeft.x + horizontalOffset, topLeft.y + verticalOffset, observer);
     *         graphics.setColor(JBColor.GRAY);
     *
     *         // Compute center of the tile
     *         drawComponentNameLabel(graphics, component.getName(), Tile.INSTANCE.width, topLeft.x, topLeft.y + height(observer) + verticalOffset);
     *         drawComponentNameLabel(graphics, component.getDescription(), Tile.INSTANCE.width, topLeft.x, topLeft.y + height(observer) + verticalOffset + getComponentNameTextHeight(graphics));
     *
     *     }
     *
     *     private int getIconHeight(ImageObserver observer) {
     *         return componentImage.getHeight(observer);
     *     }
     *
     *     private int getIconWidth(ImageObserver observer) {
     *         return componentImage.getWidth(observer);
     *     }
     *
     *     private int getComponentNameTextHeight(Graphics graphics) {
     *         Rectangle2D stringBounds = graphics.getFontMetrics().getStringBounds("Sample", graphics);
     *         return (int) stringBounds.getHeight();
     *     }
     *
     *     private int getComponentDescriptionTextHeight(Graphics graphics) {
     *         Rectangle2D stringBounds = graphics.getFontMetrics().getStringBounds("Sample", graphics);
     *         return (int) stringBounds.getHeight();
     *     }
     *
     *
     *     private void drawComponentNameLabel(Graphics g, String s, int width, int XPos, int YPos) {
     *         Rectangle2D stringBounds = g.getFontMetrics().getStringBounds(s, g);
     *         int stringWidth = (int) stringBounds.getWidth();
     *         int stringHeight = (int) stringBounds.getHeight();
     *         int startX = width / 2 - stringWidth / 2;
     *         int startY = YPos + stringHeight + COMPONENT_LABEL_PADDING_TOP;
     *         g.drawString(s, startX + XPos, startY);
     *     }
     */
}
