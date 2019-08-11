package com.reedelk.plugin.editor.designer.widget;

import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.commons.Half;
import com.reedelk.plugin.commons.Images;
import com.reedelk.plugin.commons.Labels;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;

public class InfoPanel {

    private final Image infoIcon;
    private final String infoText;

    public InfoPanel(String infoText, Image infoIcon) {
        this.infoText = infoText;
        this.infoIcon = infoIcon;
    }

    public void draw(Graphics2D g2, JComponent parent, ImageObserver observer) {
        parent.setBackground(Colors.PROPERTIES_EMPTY_SELECTION_BACKGROUND);

        int width = infoIcon.getWidth(observer);
        int height = infoIcon.getHeight(observer);
        int topLeftX = Half.of(parent.getWidth()) - Half.of(width);
        int topLeftY = Half.of(parent.getHeight()) - Half.of(height);

        Rectangle2D stringBounds = g2.getFontMetrics().getStringBounds(infoText, g2);

        g2.setColor(Colors.PROPERTIES_EMPTY_SELECTION_TEXT);
        g2.drawImage(infoIcon, topLeftX, topLeftY, parent);
        g2.drawString(infoText,
                Half.of(parent.getWidth()) - Half.of(stringBounds.getWidth()),
                topLeftY + height + Half.of(stringBounds.getHeight()));
    }

    public static class BuildingFlowInfoPanel extends InfoPanel {
        public BuildingFlowInfoPanel() {
            super(Labels.DESIGNER_INFO_BUILDING_FLOW, Images.Flow.Loading);
        }
    }

    public static class FlowWithErrorInfoPanel extends InfoPanel {
        public FlowWithErrorInfoPanel() {
            super(Labels.DESIGNER_INFO_FLOW_CONTAINS_ERRORS, Images.Flow.Error);
        }
    }
}
