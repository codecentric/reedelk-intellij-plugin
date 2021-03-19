package de.codecentric.reedelk.plugin.editor.designer.icon;

import de.codecentric.reedelk.plugin.commons.Images;

import java.awt.*;
import java.awt.image.ImageObserver;

public class IconRemoveComponent {

    private static final int ICON_WIDTH = 14;
    private static final int ICON_HEIGHT = 14;

    private int x;
    private int y;

    public void draw(Graphics2D graphics, ImageObserver observer) {
        graphics.drawImage(Images.Component.RemoveComponent, x, y, observer);
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
        int leftX = this.x;
        int rightX = this.x + ICON_WIDTH;
        int topY = this.y;
        int bottomY = this.y + ICON_HEIGHT;

        boolean withinX = x >= leftX && x <= rightX;
        boolean withinY = y >= topY && y <= bottomY;
        return withinX && withinY;
    }
}
