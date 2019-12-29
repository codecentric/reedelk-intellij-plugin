package com.reedelk.plugin.component.type.placeholder;

import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.editor.designer.AbstractGraphNode;
import com.reedelk.plugin.editor.designer.DrawableListener;

import java.awt.*;
import java.awt.event.MouseEvent;
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
    public boolean isDraggable() {
        // Placeholders cannot be dragged.
        return false;
    }

    @Override
    public boolean isSelectable() {
        // Placeholders cannot be selected.
        return false;
    }

    @Override
    public void mouseMoved(DrawableListener listener, MouseEvent event) {
        // we don't want to change the icon of the mouse
        // pointer for placeholder nodes since they cannot
        // be dragged or selected.
    }
}
