package com.esb.plugin.designer.graph.handler;

import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.Node;

abstract class AbstractDrawable implements Node {

    private final Component component;
    private int x;
    private int y;

    public AbstractDrawable(Component component) {
        this.component = component;
    }

    @Override
    public Component component() {
        return component;
    }

    @Override
    public String stringValue() {
        return null;
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int x() {
        return x;
    }

    @Override
    public int y() {
        return y;
    }
}
