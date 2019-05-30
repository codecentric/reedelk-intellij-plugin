package com.esb.plugin.editor.designer.widget;

import com.esb.plugin.graph.FlowGraph;

import java.awt.*;
import java.awt.image.ImageObserver;

public interface Widget {

    default void setPosition(int x, int y) {

    }

    void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer);

    default int width(Graphics2D graphics) {
        return 0;
    }

    default int height(Graphics2D graphics) {
        return 0;
    }

    default boolean contains(ImageObserver observer, int x, int y) {
        return false;
    }
}
