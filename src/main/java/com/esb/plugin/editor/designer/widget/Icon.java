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
    private final ComponentTitle componentTitle;
    private final ComponentDescription componentDescription;


    private int x;
    private int y;

    public Icon(ComponentData componentData) {
        image = componentData.getComponentImage();
        componentTitle = new ComponentTitle(componentData);
        componentDescription = new ComponentDescription(componentData);
    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {

        int iconDrawableHeight = height(graphics);
        int halfIconDrawableHeight = Math.floorDiv(iconDrawableHeight, 2);

        int componentTitleHeight = componentTitle.height(graphics);
        int halfComponentTitleHeight = Math.floorDiv(componentTitleHeight, 2);

        int componentDescriptionHeight = componentDescription.height(graphics);
        int halfComponentDescriptionHeight = Math.floorDiv(componentDescriptionHeight, 2);

        int totalHeight = iconDrawableHeight + componentTitleHeight + componentDescriptionHeight;
        int halfTotalHeight = Math.floorDiv(totalHeight, 2);

        // Center icon
        int centerIconY = y - halfTotalHeight + halfIconDrawableHeight;
        setPosition(x, centerIconY);

        // Center title below icon
        int centerTitleY = y - halfTotalHeight + iconDrawableHeight + halfComponentTitleHeight;
        componentTitle.setPosition(x, centerTitleY);

        // Center description below title
        int centerDescriptionY = y - halfTotalHeight + iconDrawableHeight + componentTitleHeight + halfComponentDescriptionHeight;
        componentDescription.setPosition(x, centerDescriptionY);

        componentTitle.draw(graph, graphics, observer);
        componentDescription.draw(graph, graphics, observer);

        int halfWidth = Math.floorDiv(image.getWidth(observer), 2);
        int halfHeight = Math.floorDiv(image.getHeight(observer), 2);

        int imageX = x - halfWidth;
        int imageY = y - halfHeight - componentTitleHeight;

        graphics.drawImage(image, imageX, imageY, observer);
    }


    @Override
    public boolean contains(ImageObserver observer, int x, int y) {
        int halfImageWidth = Math.floorDiv(image.getWidth(observer), 2);
        int halfImageHeight = Math.floorDiv(image.getHeight(observer), 2);

        boolean containsOnXAxis =
                x >= this.x - halfImageWidth &&
                        x <= this.x + halfImageWidth;

        boolean containsOnYAxis =
                y >= this.y - halfImageHeight &&
                        y <= this.y + halfImageHeight;
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
        int baryY = y - Math.floorDiv(height(graphics), 2) + componentTitle.height(graphics);
        return new Point(baryX, baryY);
    }

}
