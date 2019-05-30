package com.esb.plugin.editor.designer.widget;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.graph.FlowGraph;

import java.awt.*;
import java.awt.image.ImageObserver;

/**
 * Component Icon + Title + Description
 */
public class Icon {

    private static final int TOP_OFFSET = 10;
    private static final int ICON_WIDTH = 60;
    private static final int ICON_HEIGHT = 60;
    private static final int HALF_ICON_WIDTH = Math.floorDiv(ICON_WIDTH, 2);
    private static final int HALF_ICON_HEIGHT = Math.floorDiv(ICON_HEIGHT, 2);

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

    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        int componentTitleHeight = textComponentTitle.height(graphics);
        int halfComponentTitleHeight = Math.floorDiv(componentTitleHeight, 2);

        int componentDescriptionHeight = textComponentDescription.height(graphics);
        int halfComponentDescriptionHeight = Math.floorDiv(componentDescriptionHeight, 2);

        // Center title below icon
        int centerTitleY = y + halfComponentTitleHeight + TOP_OFFSET;
        textComponentTitle.setPosition(x, centerTitleY);

        // Center description below title
        int centerDescriptionY = y + componentTitleHeight + halfComponentDescriptionHeight + TOP_OFFSET;
        textComponentDescription.setPosition(x, centerDescriptionY);

        textComponentTitle.draw(graph, graphics, observer);
        textComponentDescription.draw(graph, graphics, observer);


        int imageX = x - HALF_ICON_WIDTH;
        int imageY = y - ICON_HEIGHT + TOP_OFFSET;

        graphics.drawImage(image, imageX, imageY, observer);
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

    public Point getBarycenter() {
        int baryX = x;
        int baryY = y - HALF_ICON_HEIGHT + TOP_OFFSET;
        return new Point(baryX, baryY);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
