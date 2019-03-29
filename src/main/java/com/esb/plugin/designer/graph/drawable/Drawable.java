package com.esb.plugin.designer.graph.drawable;

import com.esb.plugin.designer.editor.component.Component;

import java.awt.*;
import java.awt.image.ImageObserver;

public interface Drawable {

    Component component();

    String displayName();

    void draw(Graphics graphics, ImageObserver observer);

    int y();

    int x();

    void setPosition(int x, int y);

    boolean contains(int x, int y);
}
