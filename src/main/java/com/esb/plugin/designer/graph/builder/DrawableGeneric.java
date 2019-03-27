package com.esb.plugin.designer.graph.builder;

import com.esb.plugin.designer.editor.component.Component;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;

public class DrawableGeneric extends AbstractDrawable {

    private final Image image = Toolkit.getDefaultToolkit().getImage("/Users/lorenzo/Desktop/esb-project-support/log.png");

    public DrawableGeneric(Component component) {
        super(component);
    }

    @Override
    public void draw(Graphics graphics, ImageObserver observer) {
        graphics.drawImage(image, x() - Math.floorDiv(image.getWidth(observer), 2), y() - Math.floorDiv(image.getHeight(observer), 2), observer);

        int textCenterX = x();
        int textTopY = y() + Math.floorDiv(image.getHeight(observer), 2);

        textTopY += drawText(graphics, stringValue(), textCenterX, textTopY);

        drawText(graphics, "A Description", textCenterX, textTopY);
    }

    @Override
    public int width(ImageObserver imageObserver) {
        return image.getWidth(imageObserver);
    }

    @Override
    public int height(ImageObserver imageObserver) {
        return image.getHeight(imageObserver);
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
}
