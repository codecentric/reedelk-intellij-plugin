package com.esb.plugin.designer.editor;

import java.awt.*;

public interface Drawable {

    void draw(Graphics g);

    boolean contains(Point point);

    Point getPosition();

    void setPosition(Point point);

    int width();

    int height();
}
