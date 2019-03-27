package com.esb.plugin.designer.graph;

import com.esb.plugin.designer.editor.component.Component;

import java.awt.*;
import java.awt.image.ImageObserver;

public interface Node {

    String stringValue();

    void setPosition(int x, int y);

    int x();

    int y();

    void draw(Graphics graphics, ImageObserver observer);

    Component component();

    int width(ImageObserver imageObserver);

    int height(ImageObserver imageObserver);
}
