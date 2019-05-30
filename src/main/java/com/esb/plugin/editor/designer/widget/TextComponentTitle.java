package com.esb.plugin.editor.designer.widget;

import com.esb.plugin.component.domain.ComponentData;
import com.intellij.ui.JBColor;

import java.awt.*;

public class TextComponentTitle extends AbstractText {

    private final String title;

    public TextComponentTitle(ComponentData componentData) {
        super(new Font(".SF NS Text", Font.PLAIN, 13));
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
