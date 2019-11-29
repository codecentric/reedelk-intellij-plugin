package com.reedelk.plugin.editor.designer.misc;

import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.commons.Half;
import com.reedelk.plugin.graph.FlowGraph;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;

public abstract class BaseInfoPanel {

    private final Image infoIcon;
    private final String infoText;

    BaseInfoPanel(String infoText, Image infoIcon) {
        this.infoText = infoText;
        this.infoIcon = infoIcon;
    }

    public void draw(@NotNull Graphics2D g2, @NotNull JComponent parent, @NotNull ImageObserver observer) {
        draw(null, g2, parent, observer);
    }

    public void draw(FlowGraph graph, @NotNull Graphics2D g2, @NotNull JComponent parent, @NotNull ImageObserver observer) {
        parent.setBackground(Colors.PROPERTIES_EMPTY_SELECTION_BACKGROUND);

        int iconWidth = infoIcon.getWidth(observer);
        int iconHeight = infoIcon.getHeight(observer);
        int topLeftX = Half.of(parent.getWidth()) - Half.of(iconWidth);
        int topLeftY = Half.of(parent.getHeight()) - Half.of(iconHeight);

        Rectangle2D stringBounds = g2.getFontMetrics().getStringBounds(infoText, g2);

        g2.setColor(Colors.FOREGROUND_TEXT);
        g2.drawImage(infoIcon, topLeftX, topLeftY, parent);

        int left = Half.of(parent.getWidth()) - Half.of(stringBounds.getWidth());
        int top = topLeftY + iconHeight + (int) Math.round(stringBounds.getHeight());
        g2.drawString(infoText, left, top);

        drawAdditionalMessage(graph, g2, parent, top);
    }

    protected void drawAdditionalMessage(FlowGraph graph,
                                         @NotNull Graphics2D g2,
                                         @NotNull JComponent parent,
                                         int yCoordinateStart) {
    }
}
