package com.esb.plugin.editor.designer.widget;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.graph.FlowGraph;

import java.awt.*;
import java.awt.image.ImageObserver;

public class Icon {

    private static final int ICON_WIDTH = 60;
    private static final int ICON_HEIGHT = 60;
    private static final int HALF_ICON_WIDTH = 30;
    private static final int HALF_ICON_HEIGHT = 30;

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
        int centerTitleY = y + halfComponentTitleHeight;
        textComponentTitle.setPosition(x, centerTitleY);

        // Center description below title
        int centerDescriptionY = y + componentTitleHeight + halfComponentDescriptionHeight;
        textComponentDescription.setPosition(x, centerDescriptionY);

        textComponentTitle.draw(graph, graphics, observer);
        textComponentDescription.draw(graph, graphics, observer);


        int imageX = x - HALF_ICON_WIDTH;
        int imageY = y - image.getHeight(observer);

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

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point getBarycenter() {
        int baryX = x;
        int baryY = y - HALF_ICON_HEIGHT;
        return new Point(baryX, baryY);
    }

}
