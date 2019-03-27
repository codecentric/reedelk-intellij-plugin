package com.esb.plugin.graph.handler;

import com.esb.plugin.designer.editor.component.Component;

import java.awt.*;
import java.awt.image.ImageObserver;

public class DrawableFork extends AbstractDrawable {

    private final Image image = Toolkit.getDefaultToolkit().getImage("/Users/lorenzo/Desktop/fork.png");

    public DrawableFork(Component component) {
        super(component);
    }

    @Override
    public void draw(Graphics graphics, ImageObserver observer) {

    }

    @Override
    public boolean contains(Point point) {
        return false;
    }

    @Override
    public int width(ImageObserver imageObserver) {
        return 0;
    }

    @Override
    public int height(ImageObserver imageObserver) {
        return 0;
    }
}
