package com.esb.plugin.component.type.placeholder;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.editor.designer.AbstractGraphNode;

import java.awt.*;
import java.awt.image.ImageObserver;

public class PlaceholderNode extends AbstractGraphNode {

    public PlaceholderNode(ComponentData componentData) {
        super(componentData);
    }

    @Override
    protected void drawRemoveComponentIcon(Graphics2D graphics, ImageObserver observer) {
        // Placeholder cannot be removed
    }

    @Override
    protected boolean withinRemoveIcon(int x, int y) {
        // Placeholder cannot be removed
        return false;
    }
}
