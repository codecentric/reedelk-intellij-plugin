package com.esb.plugin.editor.designer;

import com.esb.plugin.graph.FlowGraph;

import java.awt.*;
import java.awt.image.ImageObserver;

public interface Drawable {

    default void drag(int x, int y) {
    }

    default void dragging() {
    }

    default void drop() {
    }

    default void setPosition(int x, int y) {
    }

    default void selected() {
    }

    default void unselected() {
    }

    void drawArrows(FlowGraph graph, Graphics2D graphics, ImageObserver observer);

    default void drawDrag(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {

    }

    default boolean isSelected() {
        return false;
    }

    default int x() {
        throw new UnsupportedOperationException();
    }

    default int y() {
        throw new UnsupportedOperationException();
    }

    int height(Graphics2D graphics);

    int width(Graphics2D graphics);

    /**
     * It is the graphical center of the Drawable. For instance,
     * A component might have an icon + text below. The bary center
     * in this case might be the center of the icon.
     *
     * @return the barycenter point of this drawable.
     */
    default Point getBarycenter() {
        throw new UnsupportedOperationException();
    }

    boolean contains(ImageObserver observer, int x, int y);

    void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer);

}
