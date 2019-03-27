package com.esb.plugin.graph.handler;

import com.esb.plugin.designer.editor.component.Component;

import java.awt.*;
import java.awt.image.ImageObserver;

public interface Drawable {

    void draw(Graphics graphics, ImageObserver observer);

    boolean contains(Point point);

    Point getPosition();

    Component component();

    int width(ImageObserver imageObserver);

    int height(ImageObserver imageObserver);
}
