package com.esb.plugin.designer.graph.builder;

import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.Node;
import com.esb.plugin.utils.ESBIcons;
import com.intellij.ui.JBColor;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;

abstract class AbstractDrawable implements Node {

    protected final Image image;
    private final Component component;
    private int x;
    private int y;

    public AbstractDrawable(Component component) {
        this.component = component;
        this.image = ESBIcons.forComponentAsImage(component.getName());
    }

    @Override
    public void draw(Graphics graphics, ImageObserver observer) {
        graphics.drawImage(image, x() - Math.floorDiv(image.getWidth(observer), 2), y() - Math.floorDiv(this.image.getHeight(observer), 2), observer);

        int textCenterX = x();
        int textTopY = y() + Math.floorDiv(image.getHeight(observer), 2);

        graphics.setColor(JBColor.GRAY);
        textTopY += drawText(graphics, stringValue(), textCenterX, textTopY);

        graphics.setColor(JBColor.LIGHT_GRAY);
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

    @Override
    public Component component() {
        return component;
    }

    @Override
    public String stringValue() {
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
