package com.esb.plugin.editor.designer.widget;

import com.esb.plugin.commons.Half;
import com.esb.plugin.component.domain.ComponentData;

import java.awt.*;
import java.awt.image.ImageObserver;

/**
 * Component Icon + Title + Description
 */
public class Icon {

    private static final int ARROW_ICON_PADDING = 5;

    private static final int TOP_PADDING = 10;
    private static final int BOTTOM_PADDING = 10;

    private static final int ICON_WIDTH = 60;
    private static final int ICON_HEIGHT = 60;
    private static final int HALF_ICON_WIDTH = Half.of(ICON_WIDTH);
    private static final int HALF_ICON_HEIGHT = Half.of(ICON_HEIGHT);

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

        int imageX = x - HALF_ICON_WIDTH;
        int imageY = y - ICON_HEIGHT;

        graphics.drawImage(image, imageX, imageY, observer);
        textComponentTitle.draw(graphics);
        textComponentDescription.draw(graphics);
    }

    public boolean contains(int x, int y) {
        boolean containsOnXAxis =
                x >= this.x - HALF_ICON_WIDTH &&
                        x <= this.x + HALF_ICON_WIDTH;
        boolean containsOnYAxis =
                y >= this.y - ICON_HEIGHT &&
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
        return ICON_HEIGHT + TOP_PADDING + BOTTOM_PADDING +
                textComponentTitle.height(graphics) +
                textComponentDescription.height(graphics);
    }

    public int bottomHalfHeight(Graphics2D graphics) {
        return textComponentTitle.height(graphics) +
                textComponentDescription.height(graphics) +
                BOTTOM_PADDING;
    }

    public int topHalfHeight(Graphics2D graphics) {
        return ICON_HEIGHT + TOP_PADDING;
    }

    // An arrow starts just right after the icon.
    public Point getSourceArrowStart() {
        int startX = x + HALF_ICON_WIDTH + ARROW_ICON_PADDING;
        int startY = y - HALF_ICON_HEIGHT;
        return new Point(startX, startY);
    }

    // An arrow ends just right before the icon.
    public Point getTargetArrowEnd() {
        int startX = x - HALF_ICON_WIDTH - ARROW_ICON_PADDING;
        int startY = y - HALF_ICON_HEIGHT;
        return new Point(startX, startY);
    }
}
