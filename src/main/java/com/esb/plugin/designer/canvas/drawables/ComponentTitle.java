package com.esb.plugin.designer.canvas.drawables;

import com.intellij.ui.JBColor;

import java.awt.*;

public class ComponentTitle extends AbstractText {

    private final String title;

    public ComponentTitle(String title) {
        this.title = title;
    }

    @Override
    protected String getText() {
        return title;
    }

    @Override
    protected Color getColor() {
        return JBColor.GRAY;
    }
}
