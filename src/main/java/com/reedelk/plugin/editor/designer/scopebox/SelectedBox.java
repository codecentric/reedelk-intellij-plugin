package com.reedelk.plugin.editor.designer.scopebox;

import com.reedelk.plugin.commons.Colors;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class SelectedBox {

    private static final int TOP_PADDING = 3;
    private static final int RIGHT_PADDING = 2;
    private static final int BOTTOM_PADDING = 8;

    protected int x;
    protected int y;

    public void draw(Graphics2D graphics, int width, int height) {
        graphics.setColor(Colors.DESIGNER_SELECTED_COMPONENT_BG);
        RoundRectangle2D.Double aDouble = new RoundRectangle2D.Double(
                x,
                y + TOP_PADDING,
                width - RIGHT_PADDING,
                height - BOTTOM_PADDING,
                10,
                10);

        graphics.fill(aDouble);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
