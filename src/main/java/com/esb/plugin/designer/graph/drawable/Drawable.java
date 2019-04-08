package com.esb.plugin.designer.graph.drawable;

import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.FlowGraph;

import java.awt.*;
import java.awt.image.ImageObserver;

public interface Drawable {

    Component component();

    String displayName();

    void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer);

    void drag(int x, int y);

    void dragging();

    void release();

    int y();

    int x();

    void setPosition(int x, int y);

    boolean contains(ImageObserver observer, int x, int y);

    int height();

    int width();

    void selected();

    void unselected();

}
