package com.esb.plugin.graph.node;

import com.esb.plugin.component.domain.ComponentClass;
import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.editor.designer.DrawableListener;
import com.esb.plugin.graph.FlowGraph;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;

public class NothingSelectedNode implements GraphNode {

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        // nothing to draw
    }

    @Override
    public void mouseMoved(DrawableListener listener, MouseEvent event) {
        // No op
    }

    @Override
    public void mousePressed(DrawableListener listener, MouseEvent event) {
        // No op
    }

    @Override
    public Point getTargetArrowEnd() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Point getSourceArrowStart() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void drag(int x, int y) {
        // No op
    }

    @Override
    public void dragging() {
        // No op
    }

    @Override
    public void drop() {
        // No op
    }

    @Override
    public void setPosition(int x, int y) {
        // No op
    }

    @Override
    public void selected() {
        // No op
    }

    @Override
    public void unselected() {
        // No op
    }

    @Override
    public void drawArrows(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void drawDrag(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        // No op
    }

    @Override
    public int x() {
        return 0;
    }

    @Override
    public int y() {
        return 0;
    }

    @Override
    public ComponentClass getComponentClass() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public int height(Graphics2D graphics) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int width(Graphics2D graphics) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int topHalfHeight(Graphics2D graphics) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int bottomHalfHeight(Graphics2D graphics) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(ImageObserver observer, int x, int y) {
        return false;
    }

    @Override
    public ComponentData componentData() {
        return null;
    }

}
