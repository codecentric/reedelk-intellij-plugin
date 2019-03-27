package com.esb.plugin.graph.handler;

import com.esb.plugin.designer.editor.component.Component;

import java.awt.*;

abstract class AbstractDrawable implements Drawable {

    private final Component component;
    private final Point point;

    public AbstractDrawable(Component component) {
        this.component = component;
        this.point = new Point();
    }

    @Override
    public Component component() {
        return component;
    }

    @Override
    public Point getPosition() {
        return point;
    }
}
