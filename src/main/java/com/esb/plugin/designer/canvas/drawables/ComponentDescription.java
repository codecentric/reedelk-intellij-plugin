package com.esb.plugin.designer.canvas.drawables;

import com.esb.plugin.designer.properties.PropertyTracker;
import com.intellij.ui.JBColor;

import java.awt.*;

public class ComponentDescription extends AbstractText {

    private final PropertyTracker tracker;

    public ComponentDescription(PropertyTracker tracker) {
        this.tracker = tracker;
    }

    @Override
    protected String getText() {
        return tracker.getValueAsString();
    }

    @Override
    protected Color getColor() {
        return JBColor.LIGHT_GRAY;
    }
}
