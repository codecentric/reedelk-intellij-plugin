package com.esb.plugin.editor.designer.widget;

import com.esb.plugin.commons.Images;

import java.awt.*;
import java.awt.image.ImageObserver;

public class RemoveComponentIcon {

    private static final int ICON_WIDTH = 16;
    private static final int ICON_HEIGHT = 16;

    private int x;
    private int y;

    public void draw(Graphics2D graphics, ImageObserver observer) {
        graphics.drawImage(Images.Component.RemoveComponent,
                x - Math.floorDiv(ICON_WIDTH, 2),
                y - Math.floorDiv(ICON_HEIGHT, 2), observer);
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
}
