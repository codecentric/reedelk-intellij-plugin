package com.esb.plugin.editor.designer.drawables;

import com.esb.plugin.component.domain.ComponentData;
import com.intellij.ui.JBColor;

import java.awt.*;

public class ComponentTitle extends AbstractText {

    private final String title;

    public ComponentTitle(ComponentData componentData) {
        this.title = componentData.getDisplayName();
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
