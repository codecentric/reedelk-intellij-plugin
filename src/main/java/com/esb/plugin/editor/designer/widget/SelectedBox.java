package com.esb.plugin.editor.designer.widget;

import com.esb.plugin.editor.designer.Drawable;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;

import java.awt.*;

public class SelectedBox {

    private static final Color SELECTED_COMPONENT_BG_COLOR = new JBColor(Gray._250, Gray._250);

    private int x;
    private int y;

    public void draw(Drawable parent, Graphics2D graphics) {
        int parentWidth = parent.width(graphics);
        int parentHeight = parent.height(graphics);
        graphics.setColor(SELECTED_COMPONENT_BG_COLOR);
        graphics.fillRect(
                x - Math.floorDiv(parentWidth, 2),
                y - Math.floorDiv(parentHeight, 2),
                parentWidth,
                parentHeight);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
