package com.esb.plugin.editor.designer.widget;

import com.esb.plugin.commons.Half;
import com.esb.plugin.commons.Images;

import java.awt.*;
import java.awt.image.ImageObserver;

public class RemoveComponentIcon {

    private static final int ICON_WIDTH = 14;
    private static final int ICON_HEIGHT = 14;

    private int x;
    private int y;

    public void draw(Graphics2D graphics, ImageObserver observer) {
        graphics.drawImage(
                Images.Component.RemoveComponent,
                x - Half.of(ICON_WIDTH),
                y - Half.of(ICON_HEIGHT), observer);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int width() {
        return ICON_WIDTH;
    }

    public int height() {
        return ICON_HEIGHT;
    }

    public boolean withinBounds(int x, int y) {
        int leftX = this.x - Half.of(ICON_WIDTH);
        int rightX = this.x + Half.of(ICON_WIDTH);
        int topY = this.y - Half.of(ICON_HEIGHT);
        int bottomY = this.y + Half.of(ICON_HEIGHT);

        boolean withinX = x >= leftX && x <= rightX;
        boolean withinY = y >= topY && y <= bottomY;

        return withinX && withinY;
    }
}
