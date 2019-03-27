package com.esb.plugin.designer.graph.handler;

import com.esb.plugin.designer.editor.component.Component;

import java.awt.*;
import java.awt.image.ImageObserver;

public class DrawableChoice extends AbstractDrawable {

    private final Image image = Toolkit.getDefaultToolkit().getImage("/Users/lorenzo/Desktop/fork.png");

    public DrawableChoice(Component component) {
        super(component);
    }

    @Override
    public void draw(Graphics graphics, ImageObserver observer) {

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
