package com.esb.plugin.designer.editor.component;

import java.awt.*;

public interface Drawable {

    void draw(Graphics g);

    boolean contains(Point point);

    Point getPosition();

    int width();

    int height();
}
