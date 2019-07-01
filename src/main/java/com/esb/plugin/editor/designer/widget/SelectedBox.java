package com.esb.plugin.editor.designer.widget;

import com.esb.plugin.commons.Half;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;

import java.awt.*;

public class SelectedBox {

    private static final int TOP_PADDING = 2;
    private static final Color SELECTED_COMPONENT_BG_COLOR = new JBColor(Gray._250, Gray._250);
    protected int x;
    protected int y;

    public void draw(GraphNode node, Graphics2D graphics) {
        int parentWidth = node.width(graphics);
        int bottomHalfHeight = node.bottomHalfHeight(graphics);
        graphics.setColor(SELECTED_COMPONENT_BG_COLOR);
        graphics.fillRect(x - Half.of(parentWidth), y + TOP_PADDING,
                parentWidth, bottomHalfHeight);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
