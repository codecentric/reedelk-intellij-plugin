package com.reedelk.plugin.editor.designer.icon;

import com.reedelk.component.descriptor.ComponentData;
import com.reedelk.plugin.commons.Half;
import com.reedelk.plugin.editor.designer.text.TextComponentDescription;
import com.reedelk.plugin.editor.designer.text.TextComponentTitle;

import java.awt.*;
import java.awt.image.ImageObserver;

/**
 * Component Icon + Title + Description
 */
public class Icon {

    private static final int ARROW_ICON_PADDING = 8;

    public static class Dimension {
        private Dimension() {
        }

        public static final int TOP_PADDING = 10;
        public static final int BOTTOM_PADDING = 10;

        public static final int ICON_WIDTH = 60;
        public static final int ICON_HEIGHT = 60;
        public static final int HALF_ICON_WIDTH = Half.of(ICON_WIDTH);
        public static final int HALF_ICON_HEIGHT = Half.of(ICON_HEIGHT);
    }

    // The Image has size 60x60
    private final Image image;
    private final TextComponentTitle textComponentTitle;
    private final TextComponentDescription textComponentDescription;

    private int x;
    private int y;


    public Icon(ComponentData componentData) {
        image = componentData.getComponentImage();
        textComponentTitle = new TextComponentTitle(componentData);
        textComponentDescription = new TextComponentDescription(componentData);
    }

    public void draw(Graphics2D graphics, ImageObserver observer) {
        int componentTitleHeight = textComponentTitle.height(graphics);

        // Center title below icon
        int centerTitleY = y;
        textComponentTitle.setPosition(x, centerTitleY);

        // Center description below title
        int centerDescriptionY = centerTitleY + componentTitleHeight;
        textComponentDescription.setPosition(x, centerDescriptionY);

        int imageX = x - Dimension.HALF_ICON_WIDTH;
        int imageY = y - Dimension.ICON_HEIGHT;

        graphics.drawImage(image, imageX, imageY, observer);
        textComponentTitle.draw(graphics);
        textComponentDescription.draw(graphics);
    }

    public boolean contains(int x, int y) {
        boolean containsOnXAxis =
                x >= this.x - Dimension.HALF_ICON_WIDTH &&
                        x <= this.x + Dimension.HALF_ICON_WIDTH;
        boolean containsOnYAxis =
                y >= this.y - Dimension.ICON_HEIGHT &&
                        y <= this.y;
        return containsOnXAxis && containsOnYAxis;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void selected() {
        this.textComponentTitle.selected();
        this.textComponentDescription.selected();
    }

    public void unselected() {
        this.textComponentTitle.unselected();
        this.textComponentDescription.unselected();
    }

    public int height(Graphics2D graphics) {
        return Dimension.ICON_HEIGHT + Dimension.TOP_PADDING + Dimension.BOTTOM_PADDING +
                textComponentTitle.height(graphics) +
                textComponentDescription.height(graphics);
    }

    public int bottomHalfHeight(Graphics2D graphics) {
        return textComponentTitle.height(graphics) +
                textComponentDescription.height(graphics) +
                Dimension.BOTTOM_PADDING;
    }

    public int topHalfHeight() {
        return Dimension.ICON_HEIGHT + Dimension.TOP_PADDING;
    }

    public int width() {
        return Dimension.ICON_WIDTH;
    }

    // An arrow starts just right after the icon.
    public Point getSourceArrowStart() {
        int startX = x + Dimension.HALF_ICON_WIDTH + ARROW_ICON_PADDING;
        int startY = y - Dimension.HALF_ICON_HEIGHT;
        return new Point(startX, startY);
    }

    // An arrow ends just right before the icon.
    public Point getTargetArrowEnd() {
        int startX = x - Dimension.HALF_ICON_WIDTH - ARROW_ICON_PADDING;
        int startY = y - Dimension.HALF_ICON_HEIGHT;
        return new Point(startX, startY);
    }
}
