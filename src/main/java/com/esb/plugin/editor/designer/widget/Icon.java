package com.esb.plugin.editor.designer.widget;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.graph.FlowGraph;

import java.awt.*;
import java.awt.image.ImageObserver;

public class Icon implements Widget {

    // The icon has size 60x60
    private static final int WIDTH = 60;
    private static final int HEIGHT = 60;

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

    @Override
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


        int imageX = x - Math.floorDiv(image.getWidth(observer), 2);
        int imageY = y - image.getHeight(observer);

        graphics.drawImage(image, imageX, imageY, observer);
    }


    @Override
    public boolean contains(ImageObserver observer, int x, int y) {
        int halfImageWidth = Math.floorDiv(image.getWidth(observer), 2);
        int imageHeight = image.getHeight(observer);

        boolean containsOnXAxis =
                x >= this.x - halfImageWidth &&
                        x <= this.x + halfImageWidth;

        boolean containsOnYAxis =
                y >= this.y - imageHeight &&
                        y <= this.y;
        return containsOnXAxis && containsOnYAxis;
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int height(Graphics2D graphics) {
        return HEIGHT;
    }

    @Override
    public int width(Graphics2D graphics) {
        return WIDTH;
    }

    public Point getBarycenter(Graphics2D graphics, ImageObserver observer) {
        int baryX = x;
        int baryY = y - Math.floorDiv(image.getHeight(observer), 2);
        return new Point(baryX, baryY);
    }

}
